package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewDto (
        UUID id,
        UUID bookId,
        String bookTitle,
        String bookThumbnailUrl,
        UUID userId,
        String userNickname,
        String content,
        int rating,
        int likeCount,
        int commentCount,
        boolean likedByMe,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}
