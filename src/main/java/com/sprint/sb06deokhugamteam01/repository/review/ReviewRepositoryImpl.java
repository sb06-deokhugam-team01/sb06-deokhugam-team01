package com.sprint.sb06deokhugamteam01.repository.review;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.domain.QComment;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchReviewRating;
import com.sprint.sb06deokhugamteam01.domain.batch.QBatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.QBatchReviewRating;
import com.sprint.sb06deokhugamteam01.domain.book.QBook;
import com.sprint.sb06deokhugamteam01.dto.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.QReview;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.dto.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.exception.review.InvalidReviewCursorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview qReview = QReview.review;
    private final QBook qBook = QBook.book;
    private final QComment qComment = QComment.comment;
    private final QBatchReviewRating qBatchReviewRating = QBatchReviewRating.batchReviewRating;

    @Override
    public Slice<Review> getReviews(ReviewSearchCondition condition, Pageable pageable) {

        int limit = condition.limit();
        List<Review> results = queryFactory
                .selectFrom(qReview)
                .where(
                        // 커서 조건
                        cursorCondition(
                                condition.cursor(),
                                condition.after(),
                                condition.ascending(),
                                condition.useRating()),
                        // 필터링 조건
                        userIdEq(condition.userId()),
                        bookIdEq(condition.bookId()),
                        keywordContains(condition.keyword()),
                        // soft delete 고려
                        qReview.isActive.isTrue(),
                        qReview.book.isActive
                )
                .orderBy(
                        // 주 정렬 조건
                        getOrderSpecifier(condition.ascending(), condition.useRating()),
                        // 보조 정렬 조건
                        getTieBreakerOrder(condition.ascending())
                )
                .limit(limit + 1) // limit보다 하나 더 요청해 hasNext 확인
                .fetch();

        // SliceImpl 반환을 위한 후처리
        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results.remove(limit); // 요청한 개수 초과분은 제거
        }

        // Pageable은 결과가 limit보다 작거나 같으면 hasNext=false로 자동 설정됨.
        return new SliceImpl<>(results, pageable, hasNext);
    }

    /**
     * where 절 predicate 메서드들.
     * predicate: 참/거짓을 판단하는 조건, 이를 만족하는 데이터만 DB에서 조회되게 함.
     */
    // 커서 기반 페이지네이션을 위한 조건 (주 커서와 보조 커서 동시 처리)
    private Predicate cursorCondition(String cursor, LocalDateTime after, boolean ascending, boolean useRating) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        // 주 커서 필드 (rating 또는 createdAt)
        if (useRating) {
            // rating 기준
            if (ascending) { // ASC: rating이 커지거나 (같으면) createdAt이 커지는 경우
                return qReview.rating.gt(Integer.parseInt(cursor)) // gt = greater than
                        .or(
                                qReview.rating.eq(Integer.parseInt(cursor)).and(qReview.createdAt.gt(after))
                        );
            } else { // DESC: rating이 작아지거나 (같으면) createdAt이 작아지는 경우
                return qReview.rating.lt(Integer.parseInt(cursor)) // lt = less than
                        .or(
                                qReview.rating.eq(Integer.parseInt(cursor)).and(qReview.createdAt.lt(after))
                        );
            }
        } else {
            // createdAt 기준
            if (ascending) { // ASC: createdAt이 커지는 경우 (오래된순)
                return qReview.createdAt.gt(after);
            } else { // DESC: createdAt이 작아지는 경우 (최신순)
                return qReview.createdAt.lt(after);
            }
        }
    }

    // 작성자 ID 완전 일치 조건
    private Predicate userIdEq(UUID userId) {
        return userId != null ? qReview.user.id.eq(userId) : null;
    }

    // 도서 ID 완전 일치 조건
    private Predicate bookIdEq(UUID bookId) {
        return bookId != null ? qReview.book.id.eq(bookId) : null;
    }

    // keyword - 도서명, 리뷰 내용, 리뷰작성자 닉네임 부분 일치 조건
    private Predicate keywordContains(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return qReview.content.containsIgnoreCase(keyword)
                .or(qReview.user.nickname.containsIgnoreCase(keyword))
                .or(qReview.book.title.containsIgnoreCase(keyword));
    }

    /**
     * orderBy 절 OrderSpecifier 생성 메서드들
     * OrderSpecifier: 어떤 필드를 어떤 방향(오름차순/내림차순)으로 정렬할지 정의하는 스펙
     */
    // 주 커서 필드 정렬 (rating 또는 createdAt)
    private OrderSpecifier<?> getOrderSpecifier(boolean ascending, boolean useRating) {
        Order order = ascending ? Order.ASC : Order.DESC;

        if (useRating) {
            return new OrderSpecifier<>(order, qReview.rating);
        } else {
            return new OrderSpecifier<>(order, qReview.createdAt);
        }
    }

    // 보조 정렬 조건 (Tie-breaker): ID를 기준으로 정렬
    private OrderSpecifier<?> getTieBreakerOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, qReview.id);
    }




    @Override
    public Slice<Review> getPopularReviews(PopularReviewSearchCondition condition, Pageable pageable) {

        int limit = condition.limit();
        boolean descending = condition.descending();
        BooleanExpression periodCondition = qBatchReviewRating.periodType.eq(condition.period());

        Predicate cursorCondition = batchPopularCursorCondition(
                condition.cursor(),
                condition.after(),
                descending
        );

        List<BatchReviewRating> batchResults = queryFactory
                .selectFrom(qBatchReviewRating)
                .join(qBatchReviewRating.review, qReview)
                .where(
                        periodCondition,
                        cursorCondition,
                        qReview.isActive.isTrue(),
                        qReview.book.isActive
                )
                .orderBy(
                        getBatchPrimaryOrderSpecifier(descending),
                        getBatchSecondaryOrderSpecifier(descending)
                )
                .limit(limit + 1)
                .fetch();

        // SliceImpl 반환을 위한 후처리
        boolean hasNext = batchResults.size() > limit;
        if (hasNext) {
            batchResults.remove(limit); // 요청한 개수 초과분은 제거
        }

        // 요청된 개수만큼만 자르기
        List<BatchReviewRating> limitedBatchResults = hasNext ? batchResults.subList(0, limit) : batchResults;

        // Review ID 추출 및 Review 엔티티 조회 (N+1 방지 대신 ID List 조회 방식 사용)
        List<UUID> reviewIds = limitedBatchResults.stream()
                .map(rating -> rating.getReview().getId())
                .collect(Collectors.toList());

        List<Review> results = queryFactory
                .selectFrom(qReview)
                .where(qReview.id.in(reviewIds))
                .fetch();

        // Batch 조회 결과의 순서를 Review에 적용
        Map<UUID, Review> reviewMap = results.stream().collect(Collectors.toMap(Review::getId, Function.identity()));
        List<Review> orderedReviews = limitedBatchResults.stream()
                .map(r -> reviewMap.get(r.getReview().getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new SliceImpl<>(orderedReviews, pageable, hasNext);
    }

    @Override
    public void deleteByBookId(UUID bookId) {

        queryFactory.update(qComment)
                .where(qComment.review.book.id.eq(bookId))
                .set(qComment.isActive, false)
                .execute();

        queryFactory.update(qReview)
                .where(qReview.book.id.eq(bookId))
                .set(qReview.isActive, false)
                .execute();

    }

    /**
     where절 Predicate 생성 메서드
     */
    // 인기 순위 커서 조건 (score와 createdAt 조합)
    private Predicate batchPopularCursorCondition(
            String cursor,
            LocalDateTime after,
            boolean descending
    ) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        try {
            Double cursorScore = Double.parseDouble(cursor);

            BooleanExpression scoreCondition;
            if (descending) {
                scoreCondition = qBatchReviewRating.score.lt(cursorScore);
            } else {
                scoreCondition = qBatchReviewRating.score.gt(cursorScore);
            }

            if (after == null) {
                return scoreCondition;
            }

            BooleanExpression tieBreakerCondition;
            if (descending) {
                tieBreakerCondition = qBatchReviewRating.score.eq(cursorScore)
                        .and(qReview.createdAt.lt(after));
            } else {
                // ASC: score가 같으면 qReview.createdAt은 더 커야 함 (더 최신 시간)
                tieBreakerCondition = qBatchReviewRating.score.eq(cursorScore)
                        .and(qReview.createdAt.gt(after));
            }

            return scoreCondition.or(tieBreakerCondition);

        } catch (NumberFormatException e) {
            throw new InvalidReviewCursorException(detailMap("cursor", cursor));
        }
    }

    /**
     OrderSpecifier 생성 메서드
     */
    // 주 정렬 조건 (점수)
    private OrderSpecifier<?> getBatchPrimaryOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, qBatchReviewRating.score);
    }

    // 보조 정렬 조건 (생성일시)
    private OrderSpecifier<?> getBatchSecondaryOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, qReview.createdAt);
    }

    private Map<String, Object> detailMap(String key, Object value) {
        Map<String, Object> details = new HashMap<>();
        details.put(key, value);
        return details;
    }
}
