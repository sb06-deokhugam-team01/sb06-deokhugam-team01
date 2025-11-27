package com.sprint.sb06deokhugamteam01.dto.book.request;

import java.time.LocalDate;

public record BookCreateRequest(
        String title,
        String author,
        String description,
        String publisher,
        LocalDate publishedDate,
        String isbn
) {
}
