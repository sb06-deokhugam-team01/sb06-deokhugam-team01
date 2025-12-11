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
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
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
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CommentServiceImpl commentService;


    @Test
    @DisplayName("댓글 등록 성공")
    void createComment_Success(){
        // given
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = User.builder().nickname("유저").build();
        ReflectionTestUtils.setField(user, "id", userId);
        Review review = Review.builder().user(user).build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        String content = "댓글";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(commentRepository.save(any(Comment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(notificationRepository.save(any(Notification.class)))
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
        verify(notificationRepository).save(any(Notification.class));
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

    @Test
    @DisplayName("댓글 논리 삭제 성공")
    void deleteComment_Success(){
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().build();
        Review review =  Review.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);
        Comment comment = Comment.builder().user(user).review(review).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId, userId);

        // then
        assertThat(comment.isActive()).isFalse();
    }
    @Test
    @DisplayName("존재하지 않는 댓글로 논리 삭제 실패")
    void deleteComment_CommentNotFound_Fail(){
        // given
        UUID userId = UUID.randomUUID();
        UUID invalidCommentId = UUID.randomUUID();

        given(commentRepository.findById(invalidCommentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(invalidCommentId, userId))
                .isInstanceOf(CommentNotFoundException.class);
    }
    @Test
    @DisplayName("요청자 아이디와 작성자 아이디 불일치로 논리 삭제 실패")
    void deleteComment_AccessDenied_Fail(){
        // given
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        User owner = User.builder().build();
        ReflectionTestUtils.setField(owner, "id", ownerId);
        Comment comment = Comment.builder().user(owner).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(commentId, anotherUserId))
                .isInstanceOf(CommentAccessDeniedException.class);
    }

    @Test
    @DisplayName("댓글 물리 삭제 성공")
    void hardDeleteComment_Success(){
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().build();
        Review review =  Review.builder().build();
        ReflectionTestUtils.setField(user, "id", userId);
        Comment comment = Comment.builder().user(user).review(review).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findByIdAndIsActiveFalse(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.hardDeleteComment(commentId, userId);

        // then
        verify(commentRepository).hardDeleteById(commentId);
    }
    @Test
    @DisplayName("존재하지 않는 댓글로 물리 삭제 실패")
    void hardDeleteComment_CommentNotFound_Fail(){
        // given
        UUID userId = UUID.randomUUID();
        UUID invalidCommentId = UUID.randomUUID();

        given(commentRepository.findByIdAndIsActiveFalse(invalidCommentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.hardDeleteComment(invalidCommentId, userId))
                .isInstanceOf(CommentNotFoundException.class);
        verify(commentRepository, never()).delete(any(Comment.class));
    }
    @Test
    @DisplayName("요청자 아이디와 작성자 아이디 불일치로 물리 삭제 실패")
    void hardDeleteComment_AccessDenied_Fail(){
        // given
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        User owner = User.builder().build();
        ReflectionTestUtils.setField(owner, "id", ownerId);
        Comment comment = Comment.builder().user(owner).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findByIdAndIsActiveFalse(commentId)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.hardDeleteComment(commentId, anotherUserId))
                .isInstanceOf(CommentAccessDeniedException.class);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 상세 정보 조회 성공")
    void getComment_Success(){
        // given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        User user = User.builder().nickname("유저").build();
        ReflectionTestUtils.setField(user, "id", userId);
        Review review = Review.builder().build();
        ReflectionTestUtils.setField(review, "id", reviewId);
        Comment comment = Comment.builder().user(user).review(review).build();
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        CommentDto result = commentService.getComment(commentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
        assertThat(result.userNickname()).isEqualTo("유저");
        assertThat(result.reviewId()).isEqualTo(reviewId);
        verify(commentRepository).findById(commentId);
    }
    @Test
    @DisplayName("존재하지 않는 댓글로 상세 정보 조회 실패")
    void getComment_CommentNotFound_Fail(){
        // given
        UUID invalidCommentId = UUID.randomUUID();

        given(commentRepository.findById(invalidCommentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.getComment(invalidCommentId))
                .isInstanceOf(CommentNotFoundException.class);
        verify(commentRepository).findById(any(UUID.class));
    }

    @Test
    @DisplayName("리뷰 댓글 목록 조회 성공")
    void getComments_Success(){
        // given
        UUID reviewId = UUID.randomUUID();
        Review review = Review.builder().build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        User userA = User.builder().nickname("유저A").build();
        ReflectionTestUtils.setField(userA, "id", UUID.randomUUID());
        User userB = User.builder().nickname("유저B").build();
        ReflectionTestUtils.setField(userB, "id", UUID.randomUUID());

        UUID commentAId = UUID.randomUUID();
        UUID commentBId = UUID.randomUUID();
        Comment commentA = Comment.builder().user(userA).review(review).build();
        Comment commentB = Comment.builder().user(userB).review(review).build();
        ReflectionTestUtils.setField(commentA, "id", commentAId);
        ReflectionTestUtils.setField(commentB, "id", commentBId);
        ReflectionTestUtils.setField(commentB, "createdAt", LocalDateTime.now());

        given(reviewRepository.existsById(reviewId)).willReturn(true);
        given(commentRepository.sliceComments(any(CommentSearchCondition.class)))
                .willReturn(new CommentSliceResult(List.of(commentA, commentB), false, 2L));

        CommentListRequest request = CommentListRequest.builder().reviewId(reviewId).build();

        // when
        CursorPageCommentResponse result = commentService.getComments(request);

        // then
        assertThat(result.content()).isInstanceOf(List.class);
        assertThat(result.content()).hasOnlyElementsOfType(CommentDto.class);
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.nextAfter()).isEqualTo(commentB.getCreatedAt());
        assertThat(result.nextCursor()).isEqualTo(commentB.getId());
        verify(commentRepository).sliceComments(any(CommentSearchCondition.class));
    }
    @Test
    @DisplayName("존재하지 않는 리뷰로 목록 조회 실패")
    void getComments_ReviewNotFound_Fail(){
        // given
        UUID invalidReviewId = UUID.randomUUID();
        given(reviewRepository.existsById(invalidReviewId)).willReturn(false);

        CommentListRequest request = CommentListRequest.builder().reviewId(invalidReviewId).build();

        // when & then
        assertThatThrownBy(() -> commentService.getComments(request))
                .isInstanceOf(ReviewNotFoundException.class);
    }
}