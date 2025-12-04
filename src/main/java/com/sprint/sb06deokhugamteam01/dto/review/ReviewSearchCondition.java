package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewSearchCondition(
        UUID userId,
        UUID bookId,
        String keyword,
        boolean ascending,
        boolean useRating,
        String cursor,
        LocalDateTime after,
        int limit,
        Pageable pageable
) {
}
