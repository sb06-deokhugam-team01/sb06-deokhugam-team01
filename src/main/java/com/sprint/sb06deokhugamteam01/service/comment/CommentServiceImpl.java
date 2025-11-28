package com.sprint.sb06deokhugamteam01.service.comment;

import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.CommentCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.CommentDto;
import com.sprint.sb06deokhugamteam01.dto.CommentUpdateRequest;
import com.sprint.sb06deokhugamteam01.exception.comment.CommentAccessDeniedException;
import com.sprint.sb06deokhugamteam01.exception.comment.CommentNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @Override
    public CommentDto createComment(CommentCreateRequest request) {
        log.debug("댓글 생성 시작: request={}", request);
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(Map.of("userId", request.userId())));
        Review review = reviewRepository.findById(request.reviewId())
                .orElseThrow(() -> new ReviewNotFoundException(Map.of("reviewId", request.reviewId())));

        Comment comment = Comment.builder().user(user).review(review).content(request.content()).build();
        commentRepository.save(comment);
        log.info("댓글 생성 완료: id={}", comment.getId());
        return CommentDto.from(comment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest request) {
        log.debug("댓글 수정 시작: id={}, request={}", commentId,  request);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(Map.of("commentId", commentId)));
        // 작성자 아이디와 요청자 아이디 불일치 시 접근 예외
        if(!comment.getUser().getId().equals(userId)){
            throw new CommentAccessDeniedException(Map.of("userId", userId));
        }
        comment.update(request.content());
        log.info("댓글 수정 완료: id={}", commentId);
        return CommentDto.from(comment);
    }
}
