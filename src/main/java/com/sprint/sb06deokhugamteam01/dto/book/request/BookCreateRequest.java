package com.sprint.sb06deokhugamteam01.dto.book.request;

import lombok.Builder;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Builder
public record BookCreateRequest(
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        String title,
        @NotBlank(message = "저자는 비어 있을 수 없습니다.")
        String author,
        @NotNull(message = "설명은 비어 있을 수 없습니다.")
        String description,
        @NotBlank(message = "출판사는 비어 있을 수 없습니다.")
        String publisher,
        @NotNull(message = "출판일은 비어 있을 수 없습니다.")
        LocalDate publishedDate,
        @NotBlank(message = "ISBN은 비어 있을 수 없습니다.")
        String isbn
) {

    public static Book fromDto(BookCreateRequest request) {

        return Book.builder()
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .publisher(request.publisher())
                .publishedDate(request.publishedDate())
                .isbn(request.isbn())
                .build();

    }

}
