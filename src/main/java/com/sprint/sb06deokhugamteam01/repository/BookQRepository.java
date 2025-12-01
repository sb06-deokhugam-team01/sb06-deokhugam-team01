package com.sprint.sb06deokhugamteam01.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.QBook;
import com.sprint.sb06deokhugamteam01.dto.book.request.PagingBookRequest;
import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookQRepository extends QuerydslJpaRepository<Book, UUID> {

    QBook qBook = QBook.book;

    default Slice<Book> findBooksByKeyword(PagingBookRequest pagingBookRequest) {

        List<Book> bookList = selectFrom(qBook)
                .where(buildPredicate(pagingBookRequest))
                .orderBy(buildOrderBy(pagingBookRequest))
                .orderBy(pagingBookRequest.direction() == PagingBookRequest.SortDirection.ASC
                        ? qBook.createdAt.asc()
                        : qBook.createdAt.desc())
                .limit(pagingBookRequest.limit() + 1)
                .fetch();

        return new SliceImpl<>(
                bookList,
                Pageable.ofSize(pagingBookRequest.limit()),
                bookList.size() > pagingBookRequest.limit()
        );

    }

    private BooleanBuilder buildPredicate(PagingBookRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        //키워드 검색 (title, isbn, author)
        if (request.keyword() != null && !request.keyword().isBlank()) {
            String keyword = "%" + request.keyword() + "%";
            builder.and(
                qBook.title.likeIgnoreCase(keyword)
                    .or(qBook.isbn.likeIgnoreCase(keyword))
                    .or(qBook.author.likeIgnoreCase(keyword))
            ).and(qBook.isActive.eq(true));
        }

        //커서 기반 페이지네이션
        if (request.cursor() != null) {

            if (request.direction() == PagingBookRequest.SortDirection.ASC) {

                //커서 기준 orderBy 필드 값 가져오기
                switch (request.orderBy()) {
                    case TITLE -> builder.and(qBook.title.gt(request.cursor()))
                            .or(qBook.title.eq(request.cursor()).and(qBook.createdAt.gt(request.after())));
                    case PUBLISHED_DATE -> builder.and(qBook.publishedDate.gt(LocalDate.parse(request.cursor())))
                            .or(qBook.publishedDate.eq(LocalDate.parse(request.cursor())).and(qBook.createdAt.gt(request.after())));
                    case RATING -> builder.and(qBook.rating.gt(Double.parseDouble(request.cursor())))
                            .or(qBook.rating.eq(Double.parseDouble(request.cursor())).and(qBook.createdAt.gt(request.after())));
                    case REVIEW_COUNT -> builder.and(qBook.reviewCount.gt(Integer.parseInt(request.cursor())))
                            .or(qBook.reviewCount.eq(Integer.parseInt(request.cursor())).and(qBook.createdAt.gt(request.after())));
                    default -> throw new IllegalArgumentException("Invalid orderBy field: " + request.orderBy());
                }

            } else {

                //커서 기준 orderBy 필드 값 가져오기
                switch (request.orderBy()) {
                    case TITLE -> builder.and(qBook.title.lt(request.cursor()))
                            .or(qBook.title.eq(request.cursor()).and(qBook.createdAt.lt(request.after())));
                    case PUBLISHED_DATE -> builder.and(qBook.publishedDate.lt(LocalDate.parse(request.cursor())))
                            .or(qBook.publishedDate.eq(LocalDate.parse(request.cursor())).and(qBook.createdAt.lt(request.after())));
                    case RATING -> builder.and(qBook.rating.lt(Double.parseDouble(request.cursor())))
                            .or(qBook.rating.eq(Double.parseDouble(request.cursor())).and(qBook.createdAt.lt(request.after())));
                    case REVIEW_COUNT -> builder.and(qBook.reviewCount.lt(Integer.parseInt(request.cursor())))
                            .or(qBook.reviewCount.eq(Integer.parseInt(request.cursor())).and(qBook.createdAt.lt(request.after())));
                    default -> throw new IllegalArgumentException("Invalid orderBy field: " + request.orderBy());
                }

            }

        }

        return builder;
    }
    
    private OrderSpecifier<?> buildOrderBy(PagingBookRequest request) {

        if (request.direction() == PagingBookRequest.SortDirection.ASC) {
            return switch (request.orderBy()) {
                case TITLE -> qBook.title.asc();
                case PUBLISHED_DATE -> qBook.publishedDate.asc();
                case RATING -> qBook.rating.asc();
                case REVIEW_COUNT -> qBook.reviewCount.asc();
            };
        } else {
            return switch (request.orderBy()) {
                case TITLE -> qBook.title.desc();
                case PUBLISHED_DATE -> qBook.publishedDate.desc();
                case RATING -> qBook.rating.desc();
                case REVIEW_COUNT -> qBook.reviewCount.desc();
            };
        }

    }

}
