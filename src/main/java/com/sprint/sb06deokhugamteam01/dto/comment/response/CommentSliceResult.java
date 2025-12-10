package com.sprint.sb06deokhugamteam01.dto.comment.response;

import com.sprint.sb06deokhugamteam01.domain.Comment;

import java.util.List;

public record CommentSliceResult(
        List<Comment> comments,
        boolean hasNext,
        Long totalElements
) {
}
