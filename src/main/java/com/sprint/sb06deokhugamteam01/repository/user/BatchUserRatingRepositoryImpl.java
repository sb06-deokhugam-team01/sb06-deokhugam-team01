package com.sprint.sb06deokhugamteam01.repository.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.batch.QBatchUserRating;
import com.sprint.sb06deokhugamteam01.dto.User.request.PowerUserRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@Repository
@RequiredArgsConstructor
public class BatchUserRatingRepositoryImpl implements CustomBatchUserRatingRepository {

    private final JPAQueryFactory queryFactory;
    private final QBatchUserRating bur = QBatchUserRating.batchUserRating;

    @Override
    public Slice<BatchUserRating> getBatchUserRatingList(PowerUserRequest request) {
        boolean ascending = request.direction().equals("ASC");

        LocalDate targetPeriodEnd = request.after() != null
                ? request.after().toLocalDate()
                : queryFactory
                        .select(bur.periodEnd.max())
                        .from(bur)
                        .where(bur.periodType.eq(request.toPeriodType()))
                        .fetchOne();

        if (targetPeriodEnd == null) {
            return new SliceImpl<>(java.util.List.of(), Pageable.ofSize(request.limit()), false);
        }

        var results = queryFactory
                .selectFrom(bur)
                .where(
                        bur.periodType.eq(request.toPeriodType()),
                        bur.periodStart.eq(request.setPeriodStart(targetPeriodEnd)),
                        bur.periodEnd.eq(targetPeriodEnd),
                        cursorCondition(request.cursor(), request.after(), ascending)
                )
                .orderBy(scoreOrder(ascending), idOrder(ascending))
                .limit(request.limit() + 1L)
                .fetch();

        boolean hasNext = results.size() > request.limit();
        if (hasNext) {
            results = results.subList(0, request.limit());
        }
        Pageable paging = Pageable.ofSize(request.limit());
        return new SliceImpl<>(results, paging, hasNext);
    }

    private OrderSpecifier<?> scoreOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, bur.score);
    }

    private OrderSpecifier<?> idOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, bur.id);
    }

    private com.querydsl.core.types.Predicate cursorCondition(String cursor, LocalDateTime after, boolean ascending) {
        if (cursor == null || after == null) {
            return null;
        }
        // cursor는 score 기준이 아니라 UUID 기반 tie-breaker로 사용
        return ascending
                ? bur.createdAt.gt(after).or(bur.createdAt.eq(after).and(bur.id.gt(java.util.UUID.fromString(cursor))))
                : bur.createdAt.lt(after).or(bur.createdAt.eq(after).and(bur.id.lt(java.util.UUID.fromString(cursor))));
    }
}
