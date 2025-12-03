package com.sprint.sb06deokhugamteam01.dto.comment;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CommentDto(
        UUID id,
        UUID reviewId,
        UUID userId,
        String userNickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentDto from(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .reviewId(comment.getReview().getId())
                .userId(comment.getUser().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
