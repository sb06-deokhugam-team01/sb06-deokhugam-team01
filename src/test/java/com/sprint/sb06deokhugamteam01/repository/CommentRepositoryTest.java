package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.config.JpaAuditingConfig;
import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.Comment;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.comment.request.CommentSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.comment.response.CommentSliceResult;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { QueryDslConfig.class,
                    JpaAuditingConfig.class}
))
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private EntityManager entityManager;

    // JPA Auditing과 updatable=false를 무시하고 시간 강제 설정
    private void forceUpdateCreatedAt(UUID id, LocalDateTime time){
        Query query = entityManager.createNativeQuery("UPDATE comments SET created_at= ?1 WHERE id= ?2");
        query.setParameter(1, time);
        query.setParameter(2, id);
        query.executeUpdate();
    }

    @Test
    @DisplayName("리뷰 댓글 목록 커서 페이징 조회 - 기본 조회 및 정렬")
    void sliceComments_Basic() {
        // given
        User userA = userRepository.save(User.builder().build());
        User userB = userRepository.save(User.builder().build());
        Book book = bookRepository.save(Book.builder().build());
        Review targetReview = reviewRepository.save(Review.builder().user(userA).book(book).build());
        Review anotherReview = reviewRepository.save(Review.builder().user(userB).book(book).build());

        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 20; i++) { // 댓글 20개 저장
            User writer = (i % 2 == 0) ? userA : userB; // 유저 섞기
            Comment comment = Comment.builder()
                    .user(writer).review(targetReview).content("댓글 " + i).build();
            commentRepository.save(comment);
            forceUpdateCreatedAt(comment.getId(), now.minusMonths(i)); // 댓글마다 다른 생성 시점
            entityManager.refresh(comment);
            comments.add(comment);
        }
        for (int i = 0; i < 3; i++) { // 다른 리뷰 댓글 저장 -> 조회 시 미포함되어야 함
            User writer = (i % 2 == 0) ? userA : userB; // 유저 섞기
            Comment comment = Comment.builder()
                    .user(writer).review(anotherReview).content("다른 리뷰의 댓글").build();
            commentRepository.save(comment);
            forceUpdateCreatedAt(comment.getId(), now);
            entityManager.refresh(comment);
        }
        Comment latest = comments.get(0);
        CommentSearchCondition condition = new CommentSearchCondition( // 검색 조건 - 첫 페이지, 기본값 DESC
                targetReview.getId(), Sort.Direction.DESC, null, null, 4
        );

        // when
        CommentSliceResult result = commentRepository.sliceComments(condition);

        // then
        assertThat(result.comments()).hasSize(4);
        assertThat(result.hasNext()).isEqualTo(true);
        assertThat(result.totalElements()).isEqualTo(20L);
        assertThat(result.comments().get(0).getId()).isEqualTo(latest.getId());
        assertThat(result.comments()).extracting(comment -> comment.getReview().getId())
                .containsOnly(targetReview.getId());
    }
    @Test
    @DisplayName("리뷰 댓글 목록 커서 페이징 조회 - 커서 동작")
    void sliceComments_Cursor() {
        // given
        User user = userRepository.save(User.builder().build());
        Review review = reviewRepository.save(Review.builder().build());

        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 20; i++) { // 댓글 20개 저장
            Comment comment = Comment.builder()
                    .user(user).review(review).content("댓글 " + i).build();
            commentRepository.save(comment);
            forceUpdateCreatedAt(comment.getId(), now.minusMonths(i));
            entityManager.refresh(comment);
            comments.add(comment);
        }

        UUID cursor = comments.get(9).getId(); // 이전 페이지의 마지막 요소
        LocalDateTime after = comments.get(9).getCreatedAt();
        CommentSearchCondition condition = new CommentSearchCondition(
                review.getId(), Sort.Direction.DESC, cursor, after, 10
        );

        // when
        CommentSliceResult result = commentRepository.sliceComments(condition);

        // then
        assertThat(result.comments()).hasSize(10);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.comments().get(0).getCreatedAt()).isBeforeOrEqualTo(after);
    }
    @Test
    @DisplayName("리뷰 댓글 목록 커서 페이징 조회 - 커서 동작")
    void sliceComments_Cursor_ASC() {
        // given
        User user = userRepository.save(User.builder().build());
        Review review = reviewRepository.save(Review.builder().build());

        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < 20; i++) { // 댓글 20개 저장
            Comment comment = Comment.builder()
                    .user(user).review(review).content("댓글 " + i).build();
            commentRepository.save(comment);
            forceUpdateCreatedAt(comment.getId(), now.minusMonths(i));
            entityManager.refresh(comment);
            comments.add(comment);
        }

        UUID cursor = comments.get(10).getId(); // 이전 페이지의 마지막 요소
        LocalDateTime after = comments.get(10).getCreatedAt();
        CommentSearchCondition condition = new CommentSearchCondition(
                review.getId(), Sort.Direction.ASC, cursor, after, 10
        );

        // when
        CommentSliceResult result = commentRepository.sliceComments(condition);

        // then
        assertThat(result.comments()).hasSize(10);
        assertThat(result.hasNext()).isEqualTo(false);
    }
    @Test
    @DisplayName("리뷰 댓글 목록 커서 페이징 조회 - 동점자 처리 (생성 시각이 같으면 ID 오름차순)")
    void sliceComments_TieBreak() {
        // given
        User user = userRepository.save(User.builder().build());
        Review review = reviewRepository.save(Review.builder().build());

        LocalDateTime sameTime = LocalDateTime.now().withNano(0);
        List<Comment> sameTimeComments = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // 생성 시각이 동일한 댓글 생성
            Comment comment = Comment.builder().user(user).review(review).content("동일한 시각의 댓글").build();
            commentRepository.save(comment);
            forceUpdateCreatedAt(comment.getId(), sameTime);
            entityManager.refresh(comment);
            sameTimeComments.add(comment);
        }
        // 예상 정렬 결과
        List<UUID> expectedSortedIds = sameTimeComments.stream().map(Comment::getId)
                .sorted(Comparator.comparing(UUID::toString)).toList();

        CommentSearchCondition condition = new CommentSearchCondition(
                review.getId(), Sort.Direction.DESC, null, null, 20
        );

        // when
        CommentSliceResult result = commentRepository.sliceComments(condition);

        // then
        assertThat(result.comments()).extracting(Comment::getCreatedAt)
                .allMatch(createdAt -> createdAt.equals(sameTime));
        assertThat(result.comments()).extracting(Comment::getId)
                .containsExactlyElementsOf(expectedSortedIds);
    }

    @Test
    @DisplayName("findById - 논리 삭제된 데이터 포함")
    void findByIdAndIsActiveFalse_Success() {
        // given
        User user = userRepository.save(User.builder().build());
        Review review = reviewRepository.save(Review.builder().build());

        Comment deletedComment = Comment.builder().user(user).review(review).content("논리 삭제된 댓글").build();
        deletedComment.markAsDeleted();
        commentRepository.save(deletedComment);

        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트를 비워 DB 접근 강제

        // when & then
        assertThat(commentRepository.findById(deletedComment.getId())).isEmpty();
        assertThat(commentRepository.findByIdAndIsActiveFalse(deletedComment.getId())).isNotEmpty();
    }
    @Test
    @DisplayName("delete - 논리 삭제된 데이터 포함")
    void hardDeleteById_Success() {
        // given
        User user = userRepository.save(User.builder().build());
        Review review = reviewRepository.save(Review.builder().build());

        Comment deletedComment = Comment.builder().user(user).review(review).content("논리 삭제된 댓글").build();
        deletedComment.markAsDeleted();
        commentRepository.save(deletedComment);

        // when
        commentRepository.hardDeleteById(deletedComment.getId());

        // then
        assertThat(commentRepository.count()).isEqualTo(0L);
        assertThat(commentRepository.findByIdAndIsActiveFalse(deletedComment.getId())).isNotPresent();
    }
    @Test
    @DisplayName("deleteAllByReview(리뷰와 연관된 모든 댓글 삭제) - 논리 삭제된 데이터 포함")
    void deleteAllByReview_Success() {
        // given
        User user = userRepository.save(User.builder().build());
        Review targetReview = reviewRepository.save(Review.builder().build());
        Review anotherReview = reviewRepository.save(Review.builder().build());

        Comment reviewCommentA = Comment.builder().review(targetReview).user(user).content("타겟 리뷰의 댓글").build();
        reviewCommentA.markAsDeleted();
        Comment reviewCommentB = Comment.builder().review(targetReview).user(user).content("타겟 리뷰의 댓글").build();
        Comment anotherReviewComment = Comment.builder().review(anotherReview).user(user).content("다른 리뷰의 댓글").build();
        commentRepository.save(reviewCommentA);
        commentRepository.save(reviewCommentB);
        commentRepository.save(anotherReviewComment);

        // when
        commentRepository.deleteAllByReview(targetReview);

        // then
        assertThat(commentRepository.findByIdAndIsActiveFalse(reviewCommentA.getId())).isNotPresent();
        assertThat(commentRepository.findByIdAndIsActiveFalse(reviewCommentB.getId())).isNotPresent();
        assertThat(commentRepository.findByIdAndIsActiveFalse(anotherReviewComment.getId())).isPresent();
    }
    @Test
    @DisplayName("deleteByReviewIn(리스트에 있는 리뷰와 연관된 모든 댓글 삭제) - 논리 삭제된 데이터 포함")
    void deleteByReviewIn_Success() {
        // given
        User user = userRepository.save(User.builder().build());
        Review reviewA = reviewRepository.save(Review.builder().build());
        Review reviewB = reviewRepository.save(Review.builder().build());
        List<Review> reviewsList = List.of(reviewA, reviewB);

        Comment comment = Comment.builder().review(reviewA).user(user).content("댓글").build();
        Comment deletedComment = Comment.builder().review(reviewB).user(user).content("논리 삭제된 댓글").build();
        deletedComment.markAsDeleted();
        commentRepository.save(comment);
        commentRepository.save(deletedComment);

        // when
        commentRepository.deleteByReviewIn(reviewsList);

        // then
        assertThat(commentRepository.findByIdAndIsActiveFalse(deletedComment.getId())).isNotPresent();
        assertThat(commentRepository.findByIdAndIsActiveFalse(comment.getId())).isNotPresent();
    }
}
