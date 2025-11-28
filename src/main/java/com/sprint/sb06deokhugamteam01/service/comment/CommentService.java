package com.sprint.sb06deokhugamteam01.service;

import com.sprint.sb06deokhugamteam01.dto.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.CommentUpdateRequest;

import java.util.UUID;

public interface CommentService {
    CommentDto createComment(CommentCreateRequest commentCreateRequest);

    CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest commentUpdateRequest);
}
