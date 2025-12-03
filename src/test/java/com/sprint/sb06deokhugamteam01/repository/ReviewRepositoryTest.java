package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.exception.review.InvalidReviewCursorException;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
                ReviewRepository.class, QueryDslConfig.class
        }
))
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em; // 데이터 준비를 위해 사용

    private User testUser1;
    private User testUser2;
    private User testRequestUser;
    private Book testBook1;
    private Book testBook2;
    private Review testReview1;
    private Review testReview2;
    private Review testReview3;
    private Review testReview4;

    @BeforeEach
    void setup() {

        reviewRepository.deleteAll();

        testUser1 = User.builder()
                .email("testUser@testUser.com")
                .nickname("testUser")
                .password("testUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        testUser1 = em.merge(testUser1);

        testUser2 = User.builder()
                .email("testUser2@testUser2.com")
                .nickname("testUser2")
                .password("testUser2")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        testUser2 = em.merge(testUser2);

        testRequestUser = User.builder()
                .email("requestUser@requestUser.com")
                .nickname("requestUser")
                .password("requestUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        testRequestUser = em.merge(testRequestUser);

        testBook1 = Book.builder()
                .title("testBook")
                .author("author")
                .publisher("publisher")
                .publishedDate(LocalDate.now())
                .build();
        testBook1 = em.merge(testBook1);

        testBook2 = Book.builder()
                .title("testBook2")
                .author("author2")
                .publisher("publisher2")
                .publishedDate(LocalDate.now())
                .build();
        testBook2 = em.merge(testBook2);

        testReview1 = Review.builder()
                .rating(5)
                .likeCount(50)
                .isActive(true)
                .user(testUser1)
                .book(testBook1)
                .content("Review 1 content")
                .createdAt(LocalDateTime.now())
                .build();
        testReview1 = em.merge(testReview1);

        testReview2 = Review.builder()
                .rating(4)
                .likeCount(40)
                .isActive(true)
                .user(testUser1)
                .book(testBook2)
                .content("Review 2 content")
                .createdAt(LocalDateTime.now())
                .build();
        testReview2 = em.merge(testReview2);

        testReview3 = Review.builder()
                .rating(3)
                .likeCount(30)
                .isActive(true)
                .user(testUser2)
                .book(testBook1)
                .content("Review 3 content")
                .createdAt(LocalDateTime.now())
                .build();
        testReview3 = em.merge(testReview3);

        testReview4 = Review.builder()
                .rating(2)
                .likeCount(20)
                .isActive(false)
                .user(testUser2)
                .book(testBook2)
                .content("Review 4 content")
                .createdAt(LocalDateTime.now())
                .build();
        testReview4 = em.merge(testReview4);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 기본 조회, 최신순 2개")
    void getReviews_default() {

        // given
        Pageable pageable = PageRequest.ofSize(2);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .limit(2)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting("id") // 시간순 정렬 확인
                .containsExactly(testReview3.getId(), testReview2.getId());
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 기본 조회, 최신순 2개, 다음 페이지")
    void getReviews_cursor_createdAt_desc() {

        // given
        Pageable pageable = PageRequest.ofSize(2);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .cursor(testReview2.getCreatedAt().toString())
                .after(testReview2.getCreatedAt())
                .limit(2)
                .build();
        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.getContent()).extracting("id") // 시간순 정렬 확인
                .containsExactly(testReview1.getId());
        assertThat(slice.hasNext()).isFalse(); // 남은 데이터 없음
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 평점 기준 오름차순, 다음 페이지")
    void getReviews_cursor_rating_desc() {

        // given
        Pageable pageable = PageRequest.ofSize(2);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .useRating(true)
                .ascending(true)
                .cursor("4")
                .after(testReview2.getCreatedAt())
                .limit(2)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview1.getId());
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 사용자 ID 및 도서 ID로 조회")
    void getReviews_filter_user_and_book() {

        // given
        Pageable pageable = PageRequest.ofSize(10);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .userId(testUser1.getId())
                .bookId(testBook1.getId())
                .limit(10)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 키워드 검색 부분 일치")
    void getReviews_filter_keyword() {

        // given
        Pageable pageable = PageRequest.ofSize(10);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .keyword("Review")
                .limit(10)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(3);
        assertThat(slice.getContent().get(0).getId()).isEqualTo(testReview3.getId());
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("인기 리뷰 다건 조회 성공 - 기본값")
    void getPopularReviews_success() {

        // given
        Pageable pageable = PageRequest.ofSize(2);
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .limit(2)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview3.getId(), testReview2.getId()); // testReview4는 soft delete됨
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("인기 리뷰 다건 조회 성공 - 내림차순, 다음페이지")
    void getPopularReviews_success_cursor_desc() {

        // given
        Pageable pageable = PageRequest.ofSize(1);
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(CursorPagePopularReviewRequest.RankCriteria.ALL_TIME)
                .descending(true)
                .cursor("15") // testReview1의 점수
                .after(testReview1.getCreatedAt())
                .limit(1)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview2.getId());
        assertThat(slice.hasNext()).isTrue();
    }

}