package com.sprint.sb06deokhugamteam01.dto.comment.request;

import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentSearchCondition(
        UUID reviewId,
        Sort.Direction direction,
        UUID cursor,
        LocalDateTime after,
        int limit
) {
}
