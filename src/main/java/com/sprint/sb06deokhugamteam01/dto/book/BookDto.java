package com.sprint.sb06deokhugamteam01.dto.book;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookDto(
        UUID id,
        String title,
        String author,
        String description,
        String publisher,
        LocalDate publishedDate,
        String isbn,
        String thumbnailUrl,
        int reviewCount,
        double rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BookDto fromEntity(Book book) {

        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .isbn(book.getIsbn())
                .thumbnailUrl(book.getThumbnailUrl())
                .reviewCount(book.getReviewCount())
                .rating(book.getRating())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();

    }

}
