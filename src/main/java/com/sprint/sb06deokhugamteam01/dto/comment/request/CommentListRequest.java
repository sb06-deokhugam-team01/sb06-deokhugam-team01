package com.sprint.sb06deokhugamteam01.dto.comment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentListRequest(
        @NotNull
        UUID reviewId,

        Sort.Direction direction,
        UUID cursor,

        @Past
        LocalDateTime after,

        @Min(1)
        Integer limit
) {
    public CommentListRequest {
        if(direction == null) direction = Sort.Direction.DESC;
        if(limit == null) limit = 50;
    }
}
