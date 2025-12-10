package com.sprint.sb06deokhugamteam01.dto.book.request;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookUpdateRequest(
    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    String title,
    @NotBlank(message = "저자는 비어 있을 수 없습니다.")
    String author,
    @NotNull(message = "설명은 비어 있을 수 없습니다.")
    String description,
    @NotBlank(message = "출판사는 비어 있을 수 없습니다.")
    String publisher,
    @NotNull(message = "출판일은 비어 있을 수 없습니다.")
    LocalDate publishedDate
) {

    public static Book fromDto(BookUpdateRequest request) {

        return Book.builder()
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .publisher(request.publisher())
                .publishedDate(request.publishedDate())
                .build();

    }

}
