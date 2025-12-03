package com.sprint.sb06deokhugamteam01.dto.comment.request;

import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentListRequest(
        UUID reviewId,
        Sort.Direction direction,
        UUID cursor,
        LocalDateTime after,
        int limit
) {
}
