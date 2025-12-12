package com.sprint.sb06deokhugamteam01.service.comment;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentListRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CommentSliceResult;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CursorPageCommentResponse;
import com.sprint.sb06deokhugamteam01.exception.comment.CommentAccessDeniedException;
import com.sprint.sb06deokhugamteam01.exception.comment.CommentNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;

    // 댓글 등록
    @Transactional
    @Override
    public CommentDto createComment(CommentCreateRequest request) {
        log.debug("댓글 생성 시작: request={}", request);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(Map.of("userId", request.userId())));
        Review review = reviewRepository.findById(request.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(Map.of("reviewId", request.reviewId())));

        Comment comment = Comment.builder().user(user).review(review).content(request.content()).build();
        review.increaseCommentCount();
        commentRepository.save(comment);

        if(!review.getUser().getId().equals(user.getId())) {
        notificationRepository.save(Notification.builder().user(review.getUser()).review(review)
                .confirmed(false).content("[" + user.getNickname() + "]님이 나의 리뷰에 댓글을 남겼습니다.").build());
        }

        log.info("댓글 생성 완료: id={}", comment.getId());
        return CommentDto.from(comment);
    }

    // 댓글 수정
    @Transactional
    @Override
    public CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest request) {
        log.debug("댓글 수정 시작: commentId={}, request={}", commentId,  request);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(Map.of("commentId", commentId)));
        // 작성자 아이디와 요청자 아이디 불일치 시 접근 예외
        if(!comment.getUser().getId().equals(userId)){
            throw new CommentAccessDeniedException(Map.of("userId", userId));
        }
        comment.update(request.content());
        log.info("댓글 수정 완료: commentId={}", commentId);
        return CommentDto.from(comment);
    }

    // 댓글 논리 삭제
    @Transactional
    @Override
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(Map.of("commentId", commentId)));
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException(Map.of("userId", userId));
        }
        comment.markAsDeleted();

        Review review = comment.getReview();
        review.decreaseCommentCount();
        reviewRepository.save(review);
        log.info("댓글 논리 삭제 완료: commentId={}", commentId);
    }

    // 댓글 물리 삭제
    @Transactional
    @Override
    public void hardDeleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndIsActiveFalse(commentId)
                .orElseThrow(() -> new CommentNotFoundException(Map.of("commentId", commentId)));
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentAccessDeniedException(Map.of("userId", userId));
        }

        Review review = comment.getReview();
        if (comment.isActive()) review.decreaseCommentCount(); // soft delete 되지 않았을 때만 카운트 낮춤.
        reviewRepository.save(review);

        commentRepository.hardDeleteById(commentId);
        log.info("댓글 물리 삭제 완료: commentId={}", commentId);
    }

    // 댓글 상세 정보 조회
    @Transactional(readOnly = true)
    @Override
    public CommentDto getComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(Map.of("commentId", commentId)));
        log.info("댓글 상세 정보 조회 완료: commentId={}", commentId);
        return CommentDto.from(comment);
    }

    // 리뷰 댓글 목록 조회
    @Transactional(readOnly = true)
    @Override
    public CursorPageCommentResponse getComments(CommentListRequest request) {
        if(!reviewRepository.existsById(request.reviewId())) {
            throw new ReviewNotFoundException(Map.of("reviewId", request.reviewId()));
        }

        CommentSliceResult result = commentRepository.sliceComments(new CommentSearchCondition(
                request.reviewId(), request.direction(), request.cursor(), request.after(), request.limit()));

        List<CommentDto> content = new ArrayList<>();
        for(Comment comment : result.comments()){
            content.add(CommentDto.from(comment));
        }

        int size = content.size();
        UUID nextCursor = (size == 0)? null : content.get(content.size()-1).id();
        LocalDateTime nextAfter = (size == 0)? null : content.get(content.size()-1).createdAt();

        // 리스트 값 하나 없애기 (nextCursor, nextAfter 가져오고)
        log.info("리뷰 댓글 목록 조회 완료: reviewId={}", request.reviewId());
        return CursorPageCommentResponse.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(content.size())
                .totalElements(result.totalElements())
                .hasNext(result.hasNext())
                .build();
    }
}