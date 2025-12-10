package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.dto.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPageReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponsePopularReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewLikeDto;
import com.sprint.sb06deokhugamteam01.exception.review.InvalidReviewCursorException;
import com.sprint.sb06deokhugamteam01.exception.review.ReviewNotFoundException;
import com.sprint.sb06deokhugamteam01.exception.user.UserNotFoundException;
import com.sprint.sb06deokhugamteam01.mapper.ReviewMapper;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.CommentRepository;
import com.sprint.sb06deokhugamteam01.repository.batch.BatchReviewRatingRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewLikeRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private BatchReviewRatingRepository batchReviewRatingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private final UUID reviewId = UUID.randomUUID();
    private final UUID reviewId2 = UUID.randomUUID();
    private final UUID requestReviewId = UUID.randomUUID();

    private final UUID userId = UUID.randomUUID();
    private final UUID requestUserId = UUID.randomUUID();

    private final UUID bookId = UUID.randomUUID();

    Review testReview;
    Review testReview2;

    Book testBook;
    Book testBook2;

    User testUser;
    User testRequestUser;

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

        testReview = Review.builder()
                .id(reviewId)
                .user(testUser)
                .book(testBook)
                .rating(4)
                .content("테스트내용")
                .likeCount(0)
                .commentCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        testReview2 = Review.builder()
                .id(reviewId2)
                .user(testUser)
                .book(testBook2)
                .rating(4)
                .content("내용")
                .likeCount(0)
                .commentCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("createReview 메서드는 호출 시 Review 객체를 생성한다.")
    void createReview_성공() {

        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(requestUserId)
                .content("테스트내용")
                .rating(5)
                .build();

        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        Review mockSavedReview = Review.builder()
                .id(UUID.randomUUID())
                .user(testRequestUser)
                .book(testBook)
                .rating(request.rating())
                .content(request.content())
                .likeCount(1)
                .commentCount(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        ReviewDto mockReviewDto = ReviewDto.builder()
                .id(mockSavedReview.getId())
                .userId(request.userId())
                .bookId(request.bookId())
                .content(request.content())
                .rating(request.rating())
                .build();

        when(reviewMapper.toDto(any(Review.class), any(User.class))).thenReturn(mockReviewDto);
        when(reviewRepository.save(any(Review.class))).thenReturn(mockSavedReview);

        // when
        ReviewDto response = reviewService.createReview(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.userId()).isEqualTo(testRequestUser.getId());
        assertThat(response.content()).isEqualTo("테스트내용");
        assertThat(response.rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("createReview 메서드는 userId에 해당하는 User를 찾을 수 없을 때 UserNotFoundException을 던진다.")
    void createReview_실패_User_없음() {
        // given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookId(bookId)
                .userId(userId)
                .content("테스트내용")
                .rating(5)
                .build();

        // 요청 사용자가 잘못됨을 시뮬레이션
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("getReview 메서드는 호출 시 ReviewDto를 반환한다.")
    void getReview_성공(){

        // given
        ReviewDto mockReviewDto = ReviewDto.builder()
                .id(reviewId)
                .userId(userId)
                .bookId(bookId)
                .bookTitle(testBook.getTitle())
                .content(testReview.getContent())
                .rating(testReview.getRating())
                .build();

        when(reviewMapper.toDto(any(Review.class), any(User.class))).thenReturn(mockReviewDto);
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        ReviewDto response = reviewService.getReview(reviewId, requestUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(reviewId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.bookTitle()).isEqualTo(testBook.getTitle());
        assertThat(response.content()).isEqualTo("테스트내용");

        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("getReview 메서드는 Review를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void getReview_실패_Review_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.getReview(reviewId, requestUserId))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(userRepository, times(1)).findById(requestUserId);
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("getReviews 메서드는 호출 시 CursorPageResponseReviewDto를 반환한다")
    void getReviews_성공() {

        // Given
        CursorPageReviewRequest request = CursorPageReviewRequest.builder()
                .userId(userId)
                .bookId(bookId)
                .keyword(null)
                .orderBy(CursorPageReviewRequest.SortField.createdAt)
                .direction(CursorPageReviewRequest.SortDirection.DESC)
                .cursor(null) // 조회 기준은 없음
                .after(null)
                .limit(50)
                .build();

        Slice<Review> mockSlice = new SliceImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, request.limit()), // Pageable 객체 (없어도 무방하나 명시적으로)
                false // hasNext가 false임을 의미
        );

        when(reviewRepository.getReviews(any(), any())).thenReturn(mockSlice);
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));

        // When
        reviewService.getReviews(request, requestUserId);

        // Then
        verify(reviewRepository, times(1)).getReviews(
                any(ReviewSearchCondition.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("getReviews 메서드는 커서 형식에 오류가 있으면 InvalidReviewCursorException를 던진다.")
    void getReviews_실패_커서_오류(){

        // given
        CursorPageReviewRequest request = CursorPageReviewRequest.builder()
                .cursor("string") // 생성시간도, 평점도 아닌 값이 들어옴
                .build();

        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));

        // when & then
        assertThatThrownBy(() -> reviewService.getReviews(request, requestUserId))
                .isInstanceOf(InvalidReviewCursorException.class);
        verify(reviewRepository, never()).getPopularReviews(any(), any());
    }

    @Test
    @DisplayName("getPopularReviews 메서드는 유효한 요청으로 호출 시 CursorPageResponsePopularReviewDto를 반환한다.")
    void getPopularReviews_성공_첫_페이지_조회() {

        // Given
        CursorPagePopularReviewRequest request = CursorPagePopularReviewRequest.builder()
                .period(PeriodType.WEEKLY) // 주간 랭킹 조회 요청
                .direction(CursorPagePopularReviewRequest.SortDirection.DESC)
                .cursor(null)
                .after(null)
                .limit(10)
                .build();

        List<Review> mockReviewList = List.of(testReview, testReview2);

        Slice<Review> mockSlice = new SliceImpl<>(
                mockReviewList,
                PageRequest.of(0, request.limit()),
                true // 다음 페이지가 존재함
        );

        when(reviewRepository.getPopularReviews(
                any(PopularReviewSearchCondition.class),
                any(Pageable.class) // Pageable 객체는 any()로 처리
        )).thenReturn(mockSlice);
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));

        // When
        CursorPageResponsePopularReviewDto response = reviewService.getPopularReviews(request, requestUserId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.nextCursor()).isEqualTo(String.valueOf(testReview2.getLikeCount()));
        assertThat(response.nextAfter()).isEqualTo(testReview2.getCreatedAt());
        assertThat(response.content()).hasSize(2);

        verify(reviewRepository, times(1)).getPopularReviews(
                any(PopularReviewSearchCondition.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("getPopularReviews 메서드는 커서 형식에 오류가 있으면 InvalidReviewCursorException를 던진다.")
    void getPopularReviews_실페_커서_오류() {

        // given
        CursorPagePopularReviewRequest request = CursorPagePopularReviewRequest.builder()
                .direction(CursorPagePopularReviewRequest.SortDirection.DESC)
                .cursor("string") // 점수가 아닌 문자열
                .after(testReview.getCreatedAt())
                .limit(10)
                .build();

        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));

        // when & then
        assertThatThrownBy(() -> reviewService.getPopularReviews(request, requestUserId))
                .isInstanceOf(InvalidReviewCursorException.class);
        verify(reviewRepository, never()).getPopularReviews(any(), any());
    }

    @Test
    @DisplayName("updateReview 메서드는 호출 시 Review 객체의 필드값을 바꾸고 ReviewDto를 반환한다.")
    void updateReview_성공(){

        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder()
                .content("수정")
                .rating(1)
                .build();

        ReviewDto mockReviewDto = ReviewDto.builder()
                .id(reviewId)
                .content(updateRequest.content())
                .rating(updateRequest.rating())
                .build();

        when(reviewMapper.toDto(any(Review.class), any(User.class))).thenReturn(mockReviewDto);

        // when
        ReviewDto response = reviewService.updateReview(reviewId, updateRequest, userId);

        // then
        assertThat(testReview.getContent()).isEqualTo(updateRequest.content());
        assertThat(testReview.getRating()).isEqualTo(updateRequest.rating());
        assertThat(response.content()).isEqualTo(updateRequest.content());
        assertThat(response.rating()).isEqualTo(updateRequest.rating());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("deleteReview 메서드는 호출 시 Review 객체의 isActive를 false로 바꾼다.")
    void deleteReview_성공(){

        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        reviewService.deleteReview(reviewId, userId);

        // then
        assertThat(testReview.isActive()).isFalse();
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("deleteReview 메서드는 User를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
    void deleteReview_실패_User_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(requestReviewId, requestUserId))
                .isInstanceOf(UserNotFoundException.class);

        verify(reviewRepository, never()).save(testReview);
    }

    @Test
    @DisplayName("hardDeleteReview 메서드는 호출 시 Review 객체를 삭제한다.")
    void hardDeleteReview_성공() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        reviewService.hardDeleteReview(reviewId, requestUserId);

        // then
        verify(reviewRepository, times(1)).delete(testReview);
        verify(commentRepository, times(1)).deleteAllByReview(testReview);
    }

    @Test
    @DisplayName("hardDeleteReview 메서드는 Review를 찾을 수 없을 때 ReviewNotFoundException을 던진다.")
    void hardDeleteReview_실패_Review_없음() {
        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.hardDeleteReview(reviewId, requestUserId))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).deleteById(reviewId);
    }

    @Test
    @DisplayName("likeReview 메서드는 호출 시 Review의 likeCount를 1 증가시키고 ReviewLikeDto를 반환한다.")
    void likeReview_성공() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // when
        ReviewLikeDto response = reviewService.likeReviewToggle(reviewId, requestUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.reviewId()).isEqualTo(reviewId);
        assertThat(response.userId()).isEqualTo(requestUserId);
        assertThat(response.liked()).isTrue();
        assertThat(testReview.getLikeCount()).isEqualTo(1);
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    @DisplayName("likeReview 메서드는 Review를 찾을 수 없을 때 ReviewNotFoundException을 던진다.")
    void likeReview_실패_Review_없음() {

        // given
        when(userRepository.findById(requestUserId)).thenReturn(Optional.of(testRequestUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty()); // 리뷰 없음

        // when & then
        assertThatThrownBy(() -> reviewService.likeReviewToggle(reviewId, requestUserId))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepository, never()).save(any());
    }
}