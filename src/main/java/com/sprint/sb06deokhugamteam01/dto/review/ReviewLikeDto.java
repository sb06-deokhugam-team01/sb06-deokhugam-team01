package com.sprint.sb06deokhugamteam01.dto.review;

import java.util.UUID;

public record ReviewLikeDto(
        UUID reviewId,
        UUID userId,
        boolean liked
) {
}
