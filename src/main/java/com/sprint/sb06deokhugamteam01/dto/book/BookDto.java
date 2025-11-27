package com.sprint.sb06deokhugamteam01.dto.book;

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
}
