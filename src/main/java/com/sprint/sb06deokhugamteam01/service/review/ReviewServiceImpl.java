package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.review.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ReviewDto createReview(ReviewCreateRequest request) {

        User user = userRepository.findById(request.userId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Book book = bookRepository.findById(request.bookId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 도서가 존재하지 않습니다."));

        // TODO 사용자는 한 도서에 대해 하나의 리뷰만 남길수 있도록 검증 필요

        Review review = Review.builder()
                .rating(request.rating())
                .content(request.content())
                .likeCount(0)
                .commentCount(0)
                .isActive(true)
                .user(user)
                .book(book)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewDto.from(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto getReview(ReviewOperationRequest request, UUID requestUserId) {
        userRepository.findById(requestUserId) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Review review = reviewRepository.findById(request.reviewId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 리뷰가 존재하지 않습니다."));

        return ReviewDto.from(review);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request, UUID requestUserId) {

        // 기본값 처리
        int limit = request.limit() != null
                ? request.limit()
                : 50;
        CursorPageReviewRequest.SortDirection sortDirection
                = request.direction() != null
                ? request.direction()
                : CursorPageReviewRequest.SortDirection.DESC;
        CursorPageReviewRequest.SortField sortField
                = request.orderBy() != null
                ? request.orderBy()
                : CursorPageReviewRequest.SortField.createdAt;

        boolean ascending = (sortDirection == CursorPageReviewRequest.SortDirection.ASC);
        boolean useRating = (sortField == CursorPageReviewRequest.SortField.rating);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, limit);

        // 필터링 조건과 JPA메서드 연결
        ReviewSearchCondition condition = ReviewSearchCondition.builder()
                .userId(request.userId())
                .bookId(request.bookId())
                .keyword(request.keyword())
                .useRating(useRating)
                .ascending(ascending)
                .cursor(request.cursor())
                .after(request.after())
                .limit(request.limit())
                .build();

        Slice<Review> slice
                = reviewRepository.getReviews(condition, pageable);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(ReviewDto::from) // TODO 조회하는 사용자가 좋아요 눌렀는지 계산 필요
                .toList();

        // 커서 페이징 후처리
        boolean hasNext = slice.hasNext();
        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!content.isEmpty() && hasNext) {
            Review lastReview = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = lastReview.getId().toString();
            nextAfter = lastReview.getCreatedAt();
        }

        return CursorPageResponseReviewDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(content.size())
                .totalElements(content.size()) // slice로는 전체 개수를 알 수 없음. 현재 슬라이스 요소 개수 반환.
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponsePopularReviewDto getPopularReviews(CursorPagePopularReviewRequest request,
                                                                UUID requestUserId
    ) {
        // 기본값 처리
        int limit = request.limit() != null
                ? request.limit()
                : 50;
        CursorPagePopularReviewRequest.SortDirection sortDirection
                = request.direction() != null
                ? request.direction()
                : CursorPagePopularReviewRequest.SortDirection.ASC;
        CursorPagePopularReviewRequest.RankCriteria period
                = request.period() != null
                ? request.period()
                : CursorPagePopularReviewRequest.RankCriteria.DAILY;

        boolean descending = (sortDirection == CursorPagePopularReviewRequest.SortDirection.DESC);

        // 커서 페이징 설정
        Pageable pageable = PageRequest.of(0, limit);

        // 필터링 조건과 JPA메서드 연결
        PopularReviewSearchCondition condition = PopularReviewSearchCondition.builder()
                .period(period)
                .descending(descending)
                .cursor(request.cursor())
                .after(request.after())
                .limit(request.limit())
                .build();

        Slice<Review> slice = reviewRepository.getPopularReviews(condition, pageable);

        // DTO로 변환
        List<ReviewDto> content = slice.getContent().stream()
                .map(ReviewDto::from) // TODO 조회하는 사용자가 좋아요 눌렀는지 계산 필요
                .toList();

        // 커서 페이징 후처리
        boolean hasNext = slice.hasNext();
        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!content.isEmpty() && hasNext) {
            Review lastReview = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = String.valueOf(lastReview.getLikeCount());
            nextAfter = lastReview.getCreatedAt();
        }

        return CursorPageResponsePopularReviewDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(content.size())
                .totalElements(content.size())
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional
    public ReviewDto updateReview(ReviewOperationRequest request,
                                  ReviewUpdateRequest updateRequest,
                                  UUID requestUserId
    ) {
        User user = userRepository.findById(requestUserId) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Review review = reviewRepository.findById(request.reviewId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 리뷰가 존재하지 않습니다."));

        // TODO 작성자 == 요청자 인지 검증 필요

        if (updateRequest.content() != null) {
            review.updateContent(updateRequest.content());
        }
        if (updateRequest.rating() != null) {
            review.updateRating(updateRequest.rating());
        }

        Review savedReview = reviewRepository.save(review);
        return ReviewDto.from(savedReview);
    }

    @Override
    @Transactional
    public void deleteReview(ReviewOperationRequest request, UUID requestUserId) {
        userRepository.findById(requestUserId) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Review review = reviewRepository.findById(request.reviewId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 리뷰가 존재하지 않습니다."));

        review.softDelete();
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void hardDeleteReview(ReviewOperationRequest request, UUID requestUserId) {
        userRepository.findById(requestUserId) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Review review = reviewRepository.findById(request.reviewId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 리뷰가 존재하지 않습니다."));

        reviewRepository.delete(review);
    }

    @Override
    @Transactional
    public ReviewLikeDto likeReview(ReviewOperationRequest request, UUID requestUserId) {

        userRepository.findById(requestUserId) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 사용자가 존재하지 않습니다."));

        Review review = reviewRepository.findById(request.reviewId()) // TODO 커스텀 예외로 대체
                .orElseThrow(() -> new IllegalArgumentException("해당 정보를 가진 리뷰가 존재하지 않습니다."));

        // TODO 유저-좋아요 정보 저장 필요
        review.increaseLikeCount();
        reviewRepository.save(review);

        return ReviewLikeDto.builder()
                .reviewId(review.getId())
                .userId(requestUserId)
                .liked(true)
                .build();

    }
}
