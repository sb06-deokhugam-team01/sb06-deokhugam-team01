package com.sprint.sb06deokhugamteam01.dto.book.request;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookUpdateRequest(
    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
    String title,
    @NotBlank(message = "저자는 비어 있을 수 없습니다.")
    @Size(max = 255, message = "저자는 최대 255자까지 입력할 수 있습니다.")
    String author,
    @NotNull(message = "설명은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "설명은 최대 1000자까지 입력할 수 있습니다.")
    String description,
    @NotBlank(message = "출판사는 비어 있을 수 없습니다.")
    @Size(max= 255, message = "출판사는 최대 255자까지 입력할 수 있습니다.")
    String publisher,
    @NotNull(message = "출판일은 비어 있을 수 없습니다.")
    LocalDate publishedDate
) {

    public static Book fromDto(BookUpdateRequest request) {

        return Book.builder()
                .title(request.title().trim())
                .author(request.author().trim())
                .description(request.description().trim())
                .publisher(request.publisher().trim())
                .publishedDate(request.publishedDate())
                .build();

    }

}
