package com.sprint.sb06deokhugamteam01.repository.notification;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.QNotification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNotification n = QNotification.notification;

    @Override
    public Slice<Notification> getNotifications(
        UUID userId,
        String cursor,
        LocalDateTime after,
        boolean ascending,
        Integer limit,
        Pageable pageable
    ) {
        int size = limit != null && limit > 0 ? limit : 20;
        Pageable paging = pageable != null ? pageable : PageRequest.of(0, size);

        List<Notification> results = queryFactory
            .selectFrom(n)
            .where(
                userIdEq(userId),
                cursorCondition(cursor, after, ascending)
            )
            .orderBy(
                createdAtOrder(ascending),
                idOrder(ascending)
            )
            .limit(size + 1L)
            .fetch();

        boolean hasNext = results.size() > size;
        if (hasNext) {
            results.remove(size);
        }

        return new SliceImpl<>(results, paging, hasNext);
    }

    private Predicate userIdEq(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must be provided to query notifications.");
        }
        return n.user.id.eq(userId);
    }

    private Predicate cursorCondition(String cursor, LocalDateTime after, boolean ascending) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        if (after == null) {
            throw new IllegalArgumentException("after must be provided when cursor is used.");
        }

        UUID cursorId;
        try {
            cursorId = UUID.fromString(cursor);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("cursor must be a valid UUID.", e);
        }

        if (ascending) {
            return n.createdAt.gt(after)
                .or(n.createdAt.eq(after).and(n.id.gt(cursorId)));
        } else {
            return n.createdAt.lt(after)
                .or(n.createdAt.eq(after).and(n.id.lt(cursorId)));
        }
    }

    private OrderSpecifier<?> createdAtOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, n.createdAt);
    }

    private OrderSpecifier<?> idOrder(boolean ascending) {
        Order order = ascending ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, n.id);
    }
}
