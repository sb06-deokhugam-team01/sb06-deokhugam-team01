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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CommentServiceImpl commentService;


    // TODO: Easy Random 사용... 추후 변경
    @Test
    @DisplayName("댓글 등록 성공")
    void createComment_Success(){
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = User.builder().nickname("유저").build();
        ReflectionTestUtils.setField(user, "id", userId);
        Review review = Review.builder().build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        String content = "댓글";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(commentRepository.save(any(Comment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, content);

        // when
        CommentDto result = commentService.createComment(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.reviewId()).isEqualTo(reviewId);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.userNickname()).isEqualTo("유저");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저로 댓글 등록 시도 시 실패")
    void createComment_NotFoundUser_Fail(){
        // given
        UUID invalidUserId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

        CommentCreateRequest request = new CommentCreateRequest(reviewId, invalidUserId, "댓글");

        // when & then
        assertThatThrownBy(() -> commentService.createComment(request))
                .isInstanceOf(UserNotFoundException.class);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("존재하지 않는 리뷰로 댓글 등록 시도 시 실패")
    void createComment_NotFoundReview_Fail(){
        // given
        UUID userId = UUID.randomUUID();
        UUID invalidReviewId = UUID.randomUUID();

        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(invalidReviewId)).willReturn(Optional.empty());

        CommentCreateRequest request = new CommentCreateRequest(invalidReviewId, userId, "댓글");

        // when & then
        assertThatThrownBy(() -> commentService.createComment(request))
                .isInstanceOf(ReviewNotFoundException.class);
        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success(){
        // given
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = User.builder().nickname("유저").build();
        ReflectionTestUtils.setField(user, "id", userId);
        Review review  = Review.builder().build();
        ReflectionTestUtils.setField(review, "id", reviewId);
        Comment comment = Comment.builder().user(user).review(review).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        String newContent = "수정된 댓글";

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        CommentUpdateRequest request = new CommentUpdateRequest(newContent);

        // when
        CommentDto result = commentService.updateComment(commentId, userId, request);

        // then
        assertThat(result.content()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 수정 실패")
    void updateComment_CommentNotFound_Fail(){
        // given
        UUID userId = UUID.randomUUID();
        UUID invalidCommentId = UUID.randomUUID();

        given(commentRepository.findById(invalidCommentId)).willReturn(Optional.empty());

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(invalidCommentId, userId, request))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("요청자 아이디와 작성자 아이디 불일치로 수정 실패")
    void updateComment_AccessDenied_Fail(){
        // given
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        User owner = User.builder().build();
        ReflectionTestUtils.setField(owner, "id", ownerId);
        Comment comment = Comment.builder().user(owner).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(commentId, anotherUserId, request))
                .isInstanceOf(CommentAccessDeniedException.class);
    }


}
