package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentListRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CursorPageCommentResponse;
import com.sprint.sb06deokhugamteam01.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("댓글 생성 요청: userId={}, reviewId={}", request.userId(), request.reviewId());
        log.debug("댓글 생성 요청 상세 데이터: {}", request);
        CommentDto createdComment = commentService.createComment(request);
        log.debug("댓글 생성 응답: commentId={}", createdComment.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdComment);
    }

    // 댓글 수정
    @PatchMapping(path = "/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") UUID commentId,
                                                    @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
                                                    @Valid @RequestBody CommentUpdateRequest request) {
        log.info("댓글 수정 요청: userId={}, commentId={}", userId, commentId);
        log.debug("댓글 수정 요청 상세 데이터: request={}", request);
        CommentDto updatedComment = commentService.updateComment(commentId, userId, request);
        log.debug("댓글 수정 응답: commentId={}", updatedComment.id());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedComment);
    }

    // 댓글 논리 삭제
    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") UUID commentId,
                                              @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        log.info("댓글 논리 삭제 요청: commentId={}, userId={}", commentId, userId);
        commentService.deleteComment(commentId, userId);
        log.debug("댓글 논리 삭제 응답: commentId={}", commentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    // 댓글 물리 삭제
    @DeleteMapping(path = "/{commentId}/hard")
    public ResponseEntity<Void> hardDeleteComment(@PathVariable("commentId") UUID commentId,
                                                  @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        log.info("댓글 물리 삭제 요청: commentId={}, userId={}", commentId, userId);
        commentService.hardDeleteComment(commentId, userId);
        log.debug("댓글 물리 삭제 응답: commentId={}", commentId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    // 댓글 상세 정보 조회
    @GetMapping(path = "/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("commentId") UUID commentId){
        log.info("댓글 상세 정보 조회 요청: commentId={}", commentId);
        CommentDto comment = commentService.getComment(commentId);
        log.debug("댓글 상세 정보 조회 응답: commentId={}", commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(comment);
    }

    // 리뷰 댓글 목록 조회
    @GetMapping
    public ResponseEntity<CursorPageCommentResponse> getComments(@RequestParam("reviewId") UUID reviewId,
                                                                 @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                                                                 @RequestParam(required = false) UUID cursor,
                                                                 @RequestParam(required = false)LocalDateTime after,
                                                                 @RequestParam(defaultValue = "50") int limit){
        log.info("리뷰 댓글 목록 조회 요청: reviewId={}", reviewId);
        CommentListRequest request = CommentListRequest.builder().reviewId(reviewId).direction(direction).cursor(cursor)
                                    .after(after).limit(limit).build();
        CursorPageCommentResponse comments = commentService.getComments(request);
        log.debug("리뷰 댓글 목록 조회 응답: reviewID={}", reviewId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(comments);
    }
}
