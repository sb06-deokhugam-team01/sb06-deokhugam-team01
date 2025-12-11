package com.sprint.sb06deokhugamteam01.service.comment;

import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentListRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CursorPageCommentResponse;

import java.util.UUID;

public interface CommentService {
    CommentDto createComment(CommentCreateRequest request);

    CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest request);

    void deleteComment(UUID commentId, UUID userId);

    void hardDeleteComment(UUID commentId, UUID userId);

    CommentDto getComment(UUID commentId);

    CursorPageCommentResponse getComments(CommentListRequest request);
}
