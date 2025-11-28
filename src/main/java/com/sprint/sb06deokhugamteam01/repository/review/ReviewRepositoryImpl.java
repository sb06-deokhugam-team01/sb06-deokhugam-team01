package com.sprint.sb06deokhugamteam01.repository.review;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.domain.review.QReview;
import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.CursorPagePopularReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview r = QReview.review;

    @Override
    public Slice<Review> getReviews(ReviewSearchCondition condition, Pageable pageable) {

        Integer limit = condition.limit();
        List<Review> results = queryFactory
                .selectFrom(r)
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
                        r.isActive.isTrue()
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
            results.remove(limit.intValue()); // 요청한 개수 초과분은 제거
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
                return r.rating.gt(Integer.parseInt(cursor)) // gt = greater than
                        .or(
                                r.rating.eq(Integer.parseInt(cursor)).and(r.createdAt.gt(after))
                        );
            } else { // DESC: rating이 작아지거나 (같으면) createdAt이 작아지는 경우
                return r.rating.lt(Integer.parseInt(cursor)) // lt = less than
                        .or(
                                r.rating.eq(Integer.parseInt(cursor)).and(r.createdAt.lt(after))
                        );
            }
        } else {
            // createdAt 기준
            if (ascending) { // ASC: createdAt이 커지는 경우 (오래된순)
                return r.createdAt.gt(after);
            } else { // DESC: createdAt이 작아지는 경우 (최신순)
                return r.createdAt.lt(after);
            }
        }
    }
    // TODO rating 기준은 (rating, createdAt, id), createdAt 기준은 (createdAt, id) 복합 인덱스 필요

    // 작성자 ID 완전 일치 조건
    private Predicate userIdEq(UUID userId) {
        return userId != null ? r.user.id.eq(userId) : null;
    }

    // 도서 ID 완전 일치 조건
    private Predicate bookIdEq(UUID bookId) {
        return bookId != null ? r.book.id.eq(bookId) : null;
    }

    // keyword - 도서명, 리뷰 내용, 리뷰작성자 닉네임 부분 일치 조건
    private Predicate keywordContains(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return r.content.containsIgnoreCase(keyword)
                .or(r.user.nickname.containsIgnoreCase(keyword))
                .or(r.book.title.containsIgnoreCase(keyword));
    }

    /**
     * orderBy 절 OrderSpecifier 생성 메서드들
     * OrderSpecifier: 어떤 필드를 어떤 방향(오름차순/내림차순)으로 정렬할지 정의하는 스펙
     */
    // 주 커서 필드 정렬 (rating 또는 createdAt)
    private OrderSpecifier<?> getOrderSpecifier(boolean ascending, boolean useRating) {
        Order order = ascending ? Order.ASC : Order.DESC;

        if (useRating) {
            return new OrderSpecifier<>(order, r.rating);
        } else {
            return new OrderSpecifier<>(order, r.createdAt);
        }
    }

    // 보조 정렬 조건 (Tie-breaker): ID를 기준으로 정렬
    private OrderSpecifier<?> getTieBreakerOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, r.id);
    }


    @Override
    public Slice<Review> getPopularReviews(PopularReviewSearchCondition condition, Pageable pageable) {

        Integer limit = condition.limit();
        boolean descending = condition.descending();

        List<Review> results = queryFactory
                .selectFrom(r)
                .where(
                        // 1. 기간 필터링 (DAILY, WEEKLY 등)
                        periodCondition(condition.period()),
                        // 2. 커서 조건 (likeCount와 createdAt 조합)
                        popularCursorCondition(
                                condition.cursor(),
                                condition.after(),
                                descending
                                ),
                        // 3. isActive 조건 (Soft Delete 처리 가정)
                        r.isActive.isTrue()
                )
                // 4. 정렬 조건 (likeCount -> createdAt)
                .orderBy(
                        getPrimaryOrderSpecifier(descending),
                        getSecondaryOrderSpecifier(descending)
                        )
                .limit(limit + 1)
                .fetch();

        // SliceImpl 반환을 위한 후처리
        boolean hasNext = results.size() > condition.limit();
        if (hasNext) {
            results.remove(limit.intValue()); // 요청한 개수 초과분은 제거
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    /**
    where절 Predicate 생성 메서드들
     */
    // 기간에 따른 createdAt 필터링 조건 생성
    private Predicate periodCondition(CursorPagePopularReviewRequest.RankCriteria period) {
        if (period == null || period == CursorPagePopularReviewRequest.RankCriteria.ALL_TIME) {
            return null;
        }

        LocalDateTime startDateTime;
        switch (period) {
            case DAILY:
                startDateTime = LocalDateTime.now().minusDays(1);
                break;
            case WEEKLY:
                startDateTime = LocalDateTime.now().minusWeeks(1);
                break;
            case MONTHLY:
                startDateTime = LocalDateTime.now().minusMonths(1);
                break;
            default:
                return null;
        }
        // startDateTime 이후에 생성된 리뷰만 포함
        return r.createdAt.goe(startDateTime); // goe = greater or equal
    }

    // 인기 순위 커서 조건 (likeCount와 createdAt 조합)
    private Predicate popularCursorCondition(String cursor, LocalDateTime after, boolean descending) {
        if (cursor == null) {
            return null; // 첫 페이지 조회
        }

        try {
            Integer cursorLikeCount = Integer.parseInt(cursor);

            if (descending) {
                // DESC: likeCount < cursor OR (likeCount == cursor AND createdAt < after)
                return r.likeCount.lt(cursorLikeCount)
                        .or(
                                r.likeCount.eq(cursorLikeCount).and(r.createdAt.lt(after))
                        );
            } else {
                // ASC: likeCount > cursor OR (likeCount == cursor AND createdAt > after)
                return r.likeCount.gt(cursorLikeCount)
                        .or(
                                r.likeCount.eq(cursorLikeCount).and(r.createdAt.gt(after))
                        );
            }
        } catch (NumberFormatException e) {
            // 커서가 유효한 정수가 아닐 경우 예외 처리
            // TODO 커스텀 예외로 대체
            throw new IllegalArgumentException("유효하지 않은 커서 형식입니다: " + cursor);
        }
    }

    /**
    OrderSpecifier 생성 메서드
     */
    // 주 정렬 조건 (Primary)
    private OrderSpecifier<?> getPrimaryOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, r.likeCount);
    }

    // 보조 정렬 조건 (Secondary/Tie-breaker)
    private OrderSpecifier<?> getSecondaryOrderSpecifier(boolean descending) {
        Order order = descending ? Order.DESC : Order.ASC;
        return new OrderSpecifier<>(order, r.createdAt);
    }
}
