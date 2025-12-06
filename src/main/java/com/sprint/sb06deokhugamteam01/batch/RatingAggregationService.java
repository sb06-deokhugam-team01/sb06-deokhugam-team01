package com.sprint.sb06deokhugamteam01.batch;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchReviewRating;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchReviewRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.user.BatchUserRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingAggregationService {

    private final BatchBookRatingRepository batchBookRatingRepository;
    private final BatchReviewRatingRepository batchReviewRatingRepository;
    private final BatchUserRatingRepository batchUserRatingRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void aggregateAllPeriods(LocalDate targetDate) {
        LocalDate dailyStart = targetDate;
        LocalDate weeklyStart = targetDate.minusDays(6);
        LocalDate monthlyStart = targetDate.minusDays(29);

        upsertForPeriod(PeriodType.DAILY, dailyStart, targetDate);
        upsertForPeriod(PeriodType.WEEKLY, weeklyStart, targetDate);
        upsertForPeriod(PeriodType.MONTHLY, monthlyStart, targetDate);
    }

    private void upsertForPeriod(PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

        Map<UUID, Double> reviewScores = upsertReviewRatings(periodType, startDate, endDate, start, endExclusive);
        Map<UUID, UUID> reviewAuthors = fetchReviewAuthors(reviewScores.keySet());

        upsertBookRatings(periodType, startDate, endDate, start, endExclusive);
        upsertUserRatings(periodType, startDate, endDate, start, endExclusive, reviewScores, reviewAuthors);
    }

    private void upsertBookRatings(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            LocalDateTime start,
            LocalDateTime endExclusive
    ) {
        List<Object[]> rows = em.createQuery(
                        "select r.book.id, count(r), avg(r.rating) "
                                + "from Review r "
                                + "where r.isActive = true and r.createdAt >= :start and r.createdAt < :end "
                                + "group by r.book.id",
                        Object[].class)
                .setParameter("start", start)
                .setParameter("end", endExclusive)
                .getResultList();

        for (Object[] row : rows) {
            UUID bookId = (UUID) row[0];
            long reviewCount = (long) row[1];
            Double avgRating = (Double) row[2];

            double score = (reviewCount * 0.4d) + (Optional.ofNullable(avgRating).orElse(0.0d) * 0.6d);

            BatchBookRating entity = batchBookRatingRepository
                    .findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(periodType, periodStart, periodEnd, bookId)
                    .orElseGet(() -> BatchBookRating.builder().build());

            entity.applySnapshot(
                    periodType,
                    periodStart,
                    periodEnd,
                    em.getReference(Book.class, bookId),
                    (int) reviewCount,
                    Optional.ofNullable(avgRating).orElse(0.0d),
                    score,
                    null
            );

            batchBookRatingRepository.save(entity);
        }
    }

    private Map<UUID, Double> upsertReviewRatings(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            LocalDateTime start,
            LocalDateTime endExclusive
    ) {
        Map<UUID, Integer> likeCounts = toCountMap(em.createQuery(
                        "select rl.review.id, count(rl) "
                                + "from ReviewLike rl "
                                + "where rl.createdAt >= :start and rl.createdAt < :end "
                                + "group by rl.review.id",
                        Object[].class)
                .setParameter("start", start)
                .setParameter("end", endExclusive)
                .getResultList());

        Map<UUID, Integer> commentCounts = toCountMap(em.createQuery(
                        "select c.review.id, count(c) "
                                + "from Comment c "
                                + "where c.createdAt >= :start and c.createdAt < :end and c.isActive = true "
                                + "group by c.review.id",
                        Object[].class)
                .setParameter("start", start)
                .setParameter("end", endExclusive)
                .getResultList());

        Set<UUID> reviewIds = new HashSet<>();
        reviewIds.addAll(likeCounts.keySet());
        reviewIds.addAll(commentCounts.keySet());

        Map<UUID, Double> reviewScores = new HashMap<>();

        for (UUID reviewId : reviewIds) {
            int likes = likeCounts.getOrDefault(reviewId, 0);
            int comments = commentCounts.getOrDefault(reviewId, 0);

            double score = (likes * 0.3d) + (comments * 0.7d);
            reviewScores.put(reviewId, score);

            BatchReviewRating entity = batchReviewRatingRepository
                    .findByPeriodTypeAndPeriodStartAndPeriodEndAndReview_Id(periodType, periodStart, periodEnd, reviewId)
                    .orElseGet(() -> BatchReviewRating.builder().build());

            entity.applySnapshot(
                    periodType,
                    periodStart,
                    periodEnd,
                    em.getReference(Review.class, reviewId),
                    likes,
                    comments,
                    score,
                    null
            );

            batchReviewRatingRepository.save(entity);
        }

        return reviewScores;
    }

    private void upsertUserRatings(
            PeriodType periodType,
            LocalDate periodStart,
            LocalDate periodEnd,
            LocalDateTime start,
            LocalDateTime endExclusive,
            Map<UUID, Double> reviewScores,
            Map<UUID, UUID> reviewAuthors
    ) {
        Map<UUID, Integer> likesMade = toCountMap(em.createQuery(
                        "select rl.user.id, count(rl) "
                                + "from ReviewLike rl "
                                + "where rl.createdAt >= :start and rl.createdAt < :end "
                                + "group by rl.user.id",
                        Object[].class)
                .setParameter("start", start)
                .setParameter("end", endExclusive)
                .getResultList());

        Map<UUID, Integer> commentsMade = toCountMap(em.createQuery(
                        "select c.user.id, count(c) "
                                + "from Comment c "
                                + "where c.createdAt >= :start and c.createdAt < :end and c.isActive = true "
                                + "group by c.user.id",
                        Object[].class)
                .setParameter("start", start)
                .setParameter("end", endExclusive)
                .getResultList());

        Map<UUID, Double> reviewPopularitySum = new HashMap<>();
        for (Map.Entry<UUID, Double> entry : reviewScores.entrySet()) {
            UUID reviewId = entry.getKey();
            UUID authorId = reviewAuthors.get(reviewId);
            if (authorId == null) {
                continue;
            }
            double existing = reviewPopularitySum.getOrDefault(authorId, 0.0d);
            reviewPopularitySum.put(authorId, existing + entry.getValue());
        }

        Set<UUID> userIds = new HashSet<>();
        userIds.addAll(likesMade.keySet());
        userIds.addAll(commentsMade.keySet());
        userIds.addAll(reviewPopularitySum.keySet());

        for (UUID userId : userIds) {
            int likes = likesMade.getOrDefault(userId, 0);
            int comments = commentsMade.getOrDefault(userId, 0);
            double popularity = reviewPopularitySum.getOrDefault(userId, 0.0d);

            double score = (popularity * 0.5d) + (likes * 0.2d) + (comments * 0.3d);

            BatchUserRating entity = batchUserRatingRepository
                    .findByPeriodTypeAndPeriodStartAndPeriodEndAndUser_Id(periodType, periodStart, periodEnd, userId)
                    .orElseGet(() -> BatchUserRating.builder().build());

            entity.applySnapshot(
                    periodType,
                    periodStart,
                    periodEnd,
                    em.getReference(User.class, userId),
                    popularity,
                    likes,
                    comments,
                    score,
                    null
            );

            batchUserRatingRepository.save(entity);
        }
    }

    private Map<UUID, UUID> fetchReviewAuthors(Set<UUID> reviewIds) {
        if (reviewIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = em.createQuery(
                        "select r.id, r.user.id from Review r where r.id in :ids",
                        Object[].class)
                .setParameter("ids", reviewIds)
                .getResultList();

        Map<UUID, UUID> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((UUID) row[0], (UUID) row[1]);
        }
        return result;
    }

    private Map<UUID, Integer> toCountMap(List<Object[]> rows) {
        Map<UUID, Integer> map = new HashMap<>();
        for (Object[] row : rows) {
            Number count = (Number) row[1];
            map.put((UUID) row[0], Math.toIntExact(count.longValue()));
        }
        return map;
    }
}
