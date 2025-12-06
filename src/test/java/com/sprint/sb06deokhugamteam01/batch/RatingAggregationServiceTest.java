package com.sprint.sb06deokhugamteam01.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.sb06deokhugamteam01.domain.ReviewLike;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchBookRating;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchReviewRating;
import com.sprint.sb06deokhugamteam01.domain.batch.BatchUserRating;
import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewLikeRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchBookRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchReviewRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.user.BatchUserRatingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RatingAggregationServiceTest {

    @Autowired
    private RatingAggregationService ratingAggregationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private BatchBookRatingRepository batchBookRatingRepository;

    @Autowired
    private BatchReviewRatingRepository batchReviewRatingRepository;

    @Autowired
    private BatchUserRatingRepository batchUserRatingRepository;

    @PersistenceContext
    private EntityManager em;

    private User author;
    private User liker;
    private User commenter;
    private Book book;
    private Review review;

    @BeforeEach
    void setUp() {
        author = userRepository.save(User.builder()
                .email("author@example.com")
                .nickname("author")
                .password("pw")
                .isActive(true)
                .build());

        liker = userRepository.save(User.builder()
                .email("liker@example.com")
                .nickname("liker")
                .password("pw")
                .isActive(true)
                .build());

        commenter = userRepository.save(User.builder()
                .email("commenter@example.com")
                .nickname("commenter")
                .password("pw")
                .isActive(true)
                .build());

        book = bookRepository.save(Book.builder()
                .title("title")
                .author("writer")
                .description("desc")
                .publisher("pub")
                .isbn("isbn")
                .build());

        review = reviewRepository.save(Review.builder()
                .book(book)
                .user(author)
                .rating(5)
                .content("great")
                .likeCount(0)
                .commentCount(0)
                .isActive(true)
                .build());

        // 좋아요 로그
        reviewLikeRepository.save(ReviewLike.builder()
                .review(review)
                .user(liker)
                .build());

        // 댓글
        commentRepository.save(com.sprint.sb06deokhugamteam01.domain.Comment.builder()
                .review(review)
                .user(commenter)
                .content("nice")
                .build());
    }

    @Test
    void aggregateAllPeriods_shouldUpsertDailyWeeklyMonthlySnapshots() {
        LocalDate targetDate = LocalDate.now();

        ratingAggregationService.aggregateAllPeriods(targetDate);

        assertDailySnapshot(targetDate);
        assertThat(batchBookRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(
                PeriodType.WEEKLY, targetDate.minusDays(6), targetDate, book.getId())).isPresent();
        assertThat(batchBookRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(
                PeriodType.MONTHLY, targetDate.minusDays(29), targetDate, book.getId())).isPresent();

        assertThat(batchReviewRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndReview_Id(
                PeriodType.WEEKLY, targetDate.minusDays(6), targetDate, review.getId())).isPresent();
        assertThat(batchReviewRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndReview_Id(
                PeriodType.MONTHLY, targetDate.minusDays(29), targetDate, review.getId())).isPresent();

        assertThat(batchUserRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndUser_Id(
                PeriodType.WEEKLY, targetDate.minusDays(6), targetDate, author.getId())).isPresent();
        assertThat(batchUserRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndUser_Id(
                PeriodType.MONTHLY, targetDate.minusDays(29), targetDate, author.getId())).isPresent();
    }

    private void assertDailySnapshot(LocalDate targetDate) {
        BatchBookRating bookRating = batchBookRatingRepository.findByPeriodTypeAndPeriodStartAndPeriodEndAndBook_Id(
                        PeriodType.DAILY, targetDate, targetDate, book.getId())
                .orElseThrow();
        assertThat(bookRating.getReviewCount()).isEqualTo(1);
        assertThat(bookRating.getAvgRating()).isEqualTo(5.0);
        assertThat(bookRating.getScore()).isEqualTo(0.4 + 3.0);

        BatchReviewRating reviewRating = batchReviewRatingRepository
                .findByPeriodTypeAndPeriodStartAndPeriodEndAndReview_Id(
                        PeriodType.DAILY, targetDate, targetDate, review.getId())
                .orElseThrow();
        assertThat(reviewRating.getLikeCount()).isEqualTo(1);
        assertThat(reviewRating.getCommentCount()).isEqualTo(1);
        assertThat(reviewRating.getScore()).isEqualTo(1.0);

        BatchUserRating userRating = batchUserRatingRepository
                .findByPeriodTypeAndPeriodStartAndPeriodEndAndUser_Id(
                        PeriodType.DAILY, targetDate, targetDate, author.getId())
                .orElseThrow();
        assertThat(userRating.getReviewPopularitySum()).isEqualTo(1.0);
        assertThat(userRating.getLikesMade()).isEqualTo(0);
        assertThat(userRating.getCommentsMade()).isEqualTo(0);
        assertThat(userRating.getScore()).isEqualTo(0.5);
    }
}
