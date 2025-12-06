package com.sprint.sb06deokhugamteam01.dto.review.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewLikeDto(
        UUID reviewId,
        UUID userId,
        boolean liked
) {
}
