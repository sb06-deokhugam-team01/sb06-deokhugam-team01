package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

@Builder
public record ReviewUpdateRequest(
        String content,
        Integer rating
) {
}
