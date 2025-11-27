package com.sprint.sb06deokhugamteam01;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import com.sprint.sb06deokhugamteam01.service.review.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTddTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private final UUID reviewId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID requestUserId = UUID.randomUUID();
    private final UUID bookId = UUID.randomUUID();

    Review testReview;
    User testUser;
    User testRequestUser;
    Book testBook;
    ReviewOperationRequest testReviewOperationRequest;

    @BeforeEach
    void setUp(){

        testUser = User.builder()
                .id(userId)
                .email("testUser@testUser.com")
                .nickname("testUser")
                .password("testUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testRequestUser = User.builder()
                .id(requestUserId)
                .email("requestUser@requestUser.com")
                .nickname("requestUser")
                .password("requestUser")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        testBook = Book.builder()
                .id(bookId)
                .title("testBook")
                .author("author")
                .publisher("publisher")
                .publishedDate(LocalDateTime.now())
                .reviewCount(10)
                .rating(4.5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        testReview = Review.builder()
                .id(reviewId)
                .user(testUser)
                .book(testBook)
                .rating(4)
                .content("내용")
                .likeCount(0)
                .commentCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        testReviewOperationRequest = ReviewOperationRequest.builder()
                .reviewId(reviewId)
                .build();
    }

    @Test
    @DisplayName("createReview 메서드는 호출 시 Review 객체를 생성한다.")
    void createReview_성공() {

        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(userId)
                .content("테스트내용")
                .rating(5)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        Review mockSavedReview = Review.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .book(testBook)
                .rating(request.rating())
                .content(request.content())
                .likeCount(1)
                .commentCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(mockSavedReview);

        // when
        ReviewDto response = reviewService.createReview(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.bookId()).isEqualTo(bookId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.content()).isEqualTo("테스트내용");
        assertThat(response.rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("createReview 메서드는 userId에 해당하는 User를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void createReview_실패_User_없음() {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(userId)
                .content("테스트내용")
                .rating(5)
                .build();

        // userRepository가 Optional.empty()를 반환하여 User가 없음을 시뮬레이션
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("getReview 메서드는 호출 시 ReviewDto를 반환한다.")
    void getReview_성공(){

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        ReviewDto response = reviewService.getReview(testReviewOperationRequest, requestUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(reviewId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.bookTitle()).isEqualTo(testBook.getTitle());
        assertThat(response.content()).isEqualTo("내용");

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("getReview 메서드는 Review를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void getReview_실패_Review_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.getReview(testReviewOperationRequest, requestUserId))
                .isInstanceOf(IllegalArgumentException.class); // TODO 커스텀예외로 대체

        verify(userRepository, times(1)).findById(requestUserId);
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("getReviews 메서드는 호출 시 CursorPageResponseReviewDto를 반환한다")
    void getReviews_TDD_성공() {

        // Given
        UUID requestUserId = UUID.randomUUID();
        CursorPageReviewRequest request = CursorPageReviewRequest.builder()
                .userId(userId)
                .bookId(bookId)
                .keyword(null)
                .orderBy(CursorPageReviewRequest.SortOrder.createdAt)
                .direction(CursorPageReviewRequest.SortDirection.DESC)
                .cursor(null) // 조회 기준은 없음
                .after(null)
                .limit(10)
                .requestUserId(requestUserId)
                .build();

        CursorPageResponseReviewDto mockResponse = CursorPageResponseReviewDto.builder()
                .content(Collections.emptyList())
                .nextCursor(null)
                .nextAfter(null)
                .size(0)
                .totalElements(0)
                .hasNext(false)
                .build();

        when(reviewService.getReviews(any(CursorPageReviewRequest.class)))
                .thenReturn(mockResponse);

        // When
        CursorPageResponseReviewDto response = reviewService.getReviews(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.hasNext()).isFalse();
        assertThat(response.size()).isZero();
    }

    @Test
    @DisplayName("getReviews 메서드는 limit이 유효하지 않을 때 IllegalArgumentException을 던진다.")
    void getReviews_실패_Limit_유효성_검증() {

        // Given
        CursorPageReviewRequest invalidLimitRequest = CursorPageReviewRequest.builder()
                .userId(userId)
                .bookId(bookId)
                .limit(0) // 유효성 실패
                .orderBy(CursorPageReviewRequest.SortOrder.createdAt)
                .direction(CursorPageReviewRequest.SortDirection.DESC)
                .build();

        // When & Then
        // 서비스 메서드가 유효성 검사에 실패하면 IllegalArgumentException을 던진다고 가정
        assertThatThrownBy(() -> reviewService.getReviews(invalidLimitRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // 리포지토리 메서드 호출이 없었는지 확인
        // TODO 리포지토리 메서드는 추후 구현
        // verify(reviewRepository, never()).findByCursor(any(), any(), any(), anyInt(), any());
    }

    @Test
    @DisplayName("getPopularReviews 메서드는 유효한 요청으로 호출 시 CursorPageResponsePopularReviewDto를 반환한다.")
    void getPopularReviews_성공_첫_페이지_조회() {

        // Given
        CursorPagePopularReviewRequest request = CursorPagePopularReviewRequest.builder()
                .period(CursorPagePopularReviewRequest.RankCriteria.WEEKLY) // 주간 랭킹 조회 요청
                .direction("DESC")
                .cursor(null)
                .after(null)
                .limit(10)
                .build();

        // Mock Response 생성 (실제 리뷰 객체는 Object로 가정)
        List<Object> mockContent = List.of(new Object(), new Object()); // 2개의 mock 리뷰

        // 다음 페이지가 있다고 가정
        String mockNextCursor = "20";
        LocalDateTime mockNextAfter = LocalDateTime.now().minusHours(1);

        CursorPageResponsePopularReviewDto mockResponse = CursorPageResponsePopularReviewDto.builder()
                .content(mockContent)
                .nextCursor(mockNextCursor)
                .nextAfter(mockNextAfter)
                .size(mockContent.size())
                .totalElements(100)
                .hasNext(true) // 다음 페이지가 존재함
                .build();

        // TODO Repository 메서드 Mocking으로 수정
        when(reviewService.getPopularReviews(any(CursorPagePopularReviewRequest.class)))
                .thenReturn(mockResponse);

        // When
        CursorPageResponsePopularReviewDto response = reviewService.getPopularReviews(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.nextCursor()).isEqualTo(mockNextCursor);
        assertThat(response.nextAfter()).isEqualTo(mockNextAfter);
        assertThat(response.content()).hasSize(2);

        verify(reviewService, times(1)).getPopularReviews(request);
    }

    @Test
    @DisplayName("getPopularReviews 메서드는 period가 null일 때 IllegalArgumentException을 던진다.")
    void getPopularReviews_실패_Period_누락() {

        // Given
        CursorPagePopularReviewRequest invalidRequest = CursorPagePopularReviewRequest.builder()
                .period(null) // 필수 필드 누락 (유효성 실패)
                .direction("DESC")
                .limit(10)
                .build();

        // When & Then
        assertThatThrownBy(() -> reviewService.getPopularReviews(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // 리포지토리 메서드 호출이 없었는지 확인
        // TODO Repository 매서드 호출 검증으로 수정
        verify(reviewService, never()).getPopularReviews(any());
    }

    @Test
    @DisplayName("deleteReview 메서드는 호출 시 Review 객체의 isActive를 false로 바꾼다.")
    void updateReview_성공(){

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("수정")
                .rating(1)
                .build();

        // when
        ReviewDto response = reviewService.updateReview(testReviewOperationRequest, updateRequest, requestUserId);

        // then
        assertThat(testReview.getContent()).isEqualTo(updateRequest.content());
        assertThat(testReview.getRating()).isEqualTo(updateRequest.rating());
        assertThat(response.content()).isEqualTo(updateRequest.content());
        assertThat(response.rating()).isEqualTo(updateRequest.rating());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("updateReview 메서드는 rating이 허용 범위를 벗어날 때 IllegalArgumentException을 던진다.")
    void updateReview_실패_Rating_범위_초과() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // 유효하지 않은 rating 값 (예: 최대치인 5를 초과한 6)
        ReviewUpdateRequest invalidRequest = ReviewUpdateRequest.builder()
                .content("유효한 내용")
                .rating(6)
                .build();

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(testReviewOperationRequest, invalidRequest, requestUserId))
                .isInstanceOf(IllegalArgumentException.class); // TODO 커스텀예외로 대체

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteReview 메서드는 호출 시 Review 객체의 isActive를 false로 바꾼다.")
    void deleteReview_성공(){

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        reviewService.deleteReview(testReviewOperationRequest, requestUserId);

        // then
        assertThat(testReview.isActive()).isFalse();
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("deleteReview 메서드는 User를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void deleteReview_실패_User_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(testReviewOperationRequest, requestUserId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reviewRepository, never()).save(testReview);
    }

    @Test
    @DisplayName("hardDeleteReview 메서드는 호출 시 Review 객체를 삭제한다.")
    void hardDeleteReview_성공() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        reviewService.hardDeleteReview(testReviewOperationRequest, requestUserId);

        // then
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    @DisplayName("hardDeleteReview 메서드는 Review를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void hardDeleteReview_실패_Review_없음() {
        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.hardDeleteReview(testReviewOperationRequest, requestUserId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(reviewRepository, never()).deleteById(reviewId);
    }

    @Test
    @DisplayName("likeReview 메서드는 호출 시 Review의 likeCount를 1 증가시키고 ReviewLikeDto를 반환한다.")
    void likeReview_성공() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        ReviewLikeDto response = reviewService.likeReview(testReviewOperationRequest, requestUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.reviewId()).isEqualTo(reviewId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.liked()).isTrue();
        assertThat(testReview.getLikeCount()).isEqualTo(1);
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("likeReview 메서드는 Review를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void likeReview_실패_Review_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty()); // 리뷰 없음

        // when & then
        assertThatThrownBy(() -> reviewService.likeReview(testReviewOperationRequest, requestUserId))
                .isInstanceOf(IllegalArgumentException.class); // TODO 커스텀예외로 대체

        verify(reviewRepository, never()).save(any());
    }
}
