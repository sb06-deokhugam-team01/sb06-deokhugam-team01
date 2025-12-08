package com.sprint.sb06deokhugamteam01.repository.book;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.QBatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.book.QBook;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingPopularBookRequest;
import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PopularBookQRepository extends QuerydslJpaRepository<BatchBookRating, UUID> {

    QBatchBookRating qBatchBookRating = QBatchBookRating.batchBookRating;
    QBook qBook = QBook.book;

    default Slice<BatchBookRating> findPopularBooksByPeriodAndCursor(PagingPopularBookRequest request) {

        List<BatchBookRating> ratingList = selectFrom(qBatchBookRating)
                .join(qBatchBookRating.book, qBook)
                .where(
                        qBatchBookRating.periodType.eq(request.period()),
                        buildPeriodPredicate(request),
                        buildCursorPredicate(request),
                        qBook.isActive
                )
                .orderBy(buildOrderBy(request))
                .limit(request.limit() + 1)
                .fetch();

        return new SliceImpl<>(
                ratingList,
                Pageable.ofSize(request.limit()),
                ratingList.size() > request.limit()
        );

    }

    private BooleanBuilder buildPeriodPredicate(PagingPopularBookRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        //기간 필터링
        Optional<LocalDate> lastDate = Optional.ofNullable(Objects.requireNonNull(selectFrom(qBatchBookRating)
                        .where(qBatchBookRating.periodType.eq(request.period()))
                        .orderBy(qBatchBookRating.periodEnd.desc())
                        .limit(1)
                        .fetchOne())
                .getPeriodEnd());

        lastDate.ifPresent(localDate -> builder.and(qBatchBookRating.periodEnd.eq(localDate)));

        return builder;
    }

    private BooleanBuilder buildCursorPredicate(PagingPopularBookRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        //커서 기반 페이지네이션
        if (request.cursor() != null) {

            if (request.direction() == PagingPopularBookRequest.SortDirection.ASC) {
                builder.and(qBook.id.gt(UUID.fromString(request.cursor())));
            } else {
                builder.and(qBook.id.lt(UUID.fromString(request.cursor())));
            }

        } else if (request.after() != null) {
            if (request.direction() == PagingPopularBookRequest.SortDirection.ASC) {
                builder.and(qBook.createdAt.gt(request.after()));
            } else {
                builder.and(qBook.createdAt.lt(request.after()));
            }
        }

        return builder;
    }

    private OrderSpecifier<?> buildOrderBy(PagingPopularBookRequest request) {

        if (request.direction() == PagingPopularBookRequest.SortDirection.ASC) {
            return qBook.createdAt.asc();
        } else {
            return qBook.createdAt.desc();
        }

    }

}
