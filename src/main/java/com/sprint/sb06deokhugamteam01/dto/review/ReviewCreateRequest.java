package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewCreateRequest (
        UUID bookId,
        UUID userId,
        String content,
        int rating
){
}
