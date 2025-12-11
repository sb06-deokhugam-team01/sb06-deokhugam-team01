package com.sprint.sb06deokhugamteam01.dto.comment.response;

import com.sprint.sb06deokhugamteam01.dto.comment.CommentDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CursorPageCommentResponse(
        List<CommentDto> content,
        UUID nextCursor,
        LocalDateTime nextAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {}
