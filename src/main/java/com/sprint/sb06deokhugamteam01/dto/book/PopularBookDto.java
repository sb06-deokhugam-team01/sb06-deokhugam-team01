package com.sprint.sb06deokhugamteam01.dto.book;

import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopularBookDto(
        String id,
        String bookId,
        String title,
        String author,
        String thumbnailUrl,
        PeriodType period,
        Long rank,
        Double score,
        Long reviewCount,
        Double rating,
        LocalDateTime createdAt
) {

    public static PopularBookDto fromEntity(Book book, BatchBookRating batchBookRating) {
        return PopularBookDto.builder()
                .id(batchBookRating.getId().toString())
                .bookId(book.getId().toString())
                .title(book.getTitle())
                .author(book.getAuthor())
                .thumbnailUrl(book.getThumbnailUrl())
                .period(batchBookRating.getPeriodType())
                .rank(Long.valueOf(batchBookRating.getRank()))
                .score(batchBookRating.getScore())
                .reviewCount((long) book.getReviewCount())
                .rating(book.getRating())
                .createdAt(batchBookRating.getCreatedAt())
                .build();

    }

}
