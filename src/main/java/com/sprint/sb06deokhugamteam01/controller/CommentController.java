package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("댓글 생성 요청: {}", request);
        CommentDto createdComment = commentService.createComment(request);
        log.debug("댓글 생성 응답: {}", createdComment);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdComment);
    }

    @PatchMapping(path = "/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") UUID commentId,
                                                    @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
                                                    @Valid @RequestBody CommentUpdateRequest request) {
        log.info("댓글 수정 요청: {}", request);
        CommentDto updatedComment = commentService.updateComment(commentId, userId, request);
        log.debug("댓글 수정 응답: {}", updatedComment);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedComment);
    }
}
