package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager em; // 데이터 준비를 위해 사용

    // 테스트용 상수
    private final UUID USER_ID_1 = UUID.randomUUID();
    private final UUID USER_ID_2 = UUID.randomUUID();
    private final UUID REQUEST_USER_ID = UUID.randomUUID();
    private final UUID BOOK_ID_A = UUID.randomUUID();
    private final UUID BOOK_ID_B = UUID.randomUUID();

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

        testUser1 = User.builder()
                .id(USER_ID_1)
                .email("testUser@testUser.com")
                .nickname("testUser")
                .password("testUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testUser2 = User.builder()
                .id(USER_ID_2)
                .email("testUser2@testUser2.com")
                .nickname("testUser2")
                .password("testUser2")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testRequestUser = User.builder()
                .id(REQUEST_USER_ID)
                .email("requestUser@requestUser.com")
                .nickname("requestUser")
                .password("requestUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testBook1 = Book.builder()
                .title("testBook")
                .author("author")
                .publisher("publisher")
                .publishedDate(LocalDate.now())
                .build();

        testBook2 = Book.builder()
                .title("testBook2")
                .author("author2")
                .publisher("publisher2")
                .publishedDate(LocalDate.now())
                .build();

        testReview1 = Review.builder()
                .id(UUID.randomUUID()) // ID 명시적 생성
                .rating(5)
                .likeCount(20)
                .isActive(true)
                .user(testUser1)
                .book(testBook1)
                .content("Review 1 content")
                .build();
        em.persist(testReview1);

        testReview2 = Review.builder()
                .id(UUID.randomUUID())
                .rating(4)
                .likeCount(10)
                .isActive(true)
                .user(testUser1)
                .book(testBook2)
                .content("Review 2 content")
                .build();
        em.persist(testReview2);

        testReview3 = Review.builder()
                .id(UUID.randomUUID())
                .rating(3)
                .likeCount(30)
                .isActive(true)
                .user(testUser2)
                .book(testBook1)
                .content("Review 3 content")
                .build();
        em.persist(testReview3);

        testReview4 = Review.builder()
                .id(UUID.randomUUID())
                .rating(2)
                .likeCount(20)
                .isActive(false) // Inactive 리뷰
                .user(testUser2)
                .book(testBook2)
                .content("Review 4 content")
                .build();
        em.persist(testReview4);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 기본 조회, 최신순 2개")
    void getReviews_default() {

        // given
        Pageable pageable = PageRequest.ofSize(3);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .limit(3)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting("id") // 시간순 정렬 확인
                .containsExactly(testReview4.getId(), testReview3.getId());
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 기본 조회, 최신순 2개, 다음 페이지")
    void getReviews_cursor_createdAt_desc() {

        // given
        Pageable pageable = PageRequest.ofSize(2);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .cursor(testReview2.getCreatedAt().toString())
                .limit(2)
                .build();
        // when
        Slice<Review> slice = reviewRepository.getReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.getContent()).extracting("id") // 시간순 정렬 확인
                .containsExactly(testReview2.getId(), testReview1.getId());
        assertThat(slice.hasNext()).isFalse(); // 남은 데이터 없음
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 평점 기준 내림차순, 다음 페이지")
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
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview3.getId(), testReview4.getId());
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("리뷰 다건 조회 성공 - 사용자 ID 및 도서 ID로 조회")
    void getReviews_filter_user_and_book() {
        // given
        Pageable pageable = PageRequest.ofSize(10);
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .userId(USER_ID_1)
                .bookId(BOOK_ID_A)
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
        assertThat(slice.getContent()).hasSize(4);
        assertThat(slice.getContent().get(0).getId()).isEqualTo(testReview4.getId());
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("인기 리뷰 다건 조회 성공 - 전체 기간 (likeCount DESC, createdAt DESC)")
    void getPopularReviews_success_allTime() {
        // given
        Pageable pageable = PageRequest.ofSize(2);
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(CursorPagePopularReviewRequest.RankCriteria.ALL_TIME)
                .descending(true)
                .limit(2)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview1.getId(), testReview2.getId());
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("인기 리뷰 다건 조회 성공 - likeCount-createdAt 복합 커서 (DESC)")
    void getPopularReviews_success_cursor_desc() {
        // given
        Pageable pageable = PageRequest.ofSize(1);
        // 커서: review1 (like=20, createdAt=BASE_TIME)
        // r1과 좋아요 수가 같거나 낮은 리뷰 중, 정렬 순서상 다음에 와야 하는 리뷰를 찾습니다.
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(CursorPagePopularReviewRequest.RankCriteria.ALL_TIME)
                .descending(true)
                .cursor("20")
                .after(testReview1.getCreatedAt())
                .limit(1)
                .build();

        // when
        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // then
        assertThat(slice.getContent()).hasSize(1);
        // likeCount < 20 인 항목 중 가장 높은 항목: r2(10) 또는 r5(10)
        // 동점 처리: r2(10, +1s), r5(10, +4s) -> DESC 정렬이므로 r2(+1s)가 먼저 와야 합니다.
        assertThat(slice.getContent()).extracting("id")
                .containsExactly(testReview2.getId());
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("인기 리뷰 다건 조회 실패 - 커서 형식 오류")
    void getPopularReviews_failure_invalid_cursor() {
        // given
        Pageable pageable = PageRequest.ofSize(1);
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(CursorPagePopularReviewRequest.RankCriteria.ALL_TIME)
                .descending(true)
                .cursor("string")
                .after(testReview1.getCreatedAt())
                .limit(1)
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewRepository.getPopularReviews(condition, pageable);
        });
    }

}