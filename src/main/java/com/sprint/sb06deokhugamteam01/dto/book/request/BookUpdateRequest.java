package com.sprint.sb06deokhugamteam01.dto.book.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BookUpdateRequest(
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate
) {
}
