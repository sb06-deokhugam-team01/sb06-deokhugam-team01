package com.sprint.sb06deokhugamteam01.dto.book.response;

import com.sprint.sb06deokhugamteam01.dto.book.BookDto;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookInfo(
        String title,
        String author,
        String description,
        String publisher,
        LocalDate publishedDate,
        String isbn,
        String thumbnailImage
) {
    public static BookInfo fromDto(BookDto bookDto) {
        return BookInfo.builder()
                .title(bookDto.title())
                .author(bookDto.author())
                .description(bookDto.description())
                .publisher(bookDto.publisher())
                .publishedDate(bookDto.publishedDate())
                .isbn(bookDto.isbn())
                .thumbnailImage(bookDto.thumbnailUrl())
                .build();
    }
}
