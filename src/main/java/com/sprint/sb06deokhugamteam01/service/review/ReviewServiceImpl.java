package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.domain.Book;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.review.*;
import com.sprint.sb06deokhugamteam01.repository.BookRepository;
import com.sprint.sb06deokhugamteam01.repository.ReviewRepository;
import com.sprint.sb06deokhugamteam01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request) {
        return null;
    }

    @Override
    public CursorPageResponsePopularReviewDto getPopularReviews(CursorPagePopularReviewRequest request) {
        return null;
    }

    @Override
    public ReviewDto updateReview(ReviewOperationRequest request,
                                  ReviewUpdateRequest updateRequest,
                                  UUID requestUserId
    ) {
        return null;
    }

    @Override
    public void deleteReview(ReviewOperationRequest request, UUID requestUserId) {

    }

    @Override
    public void hardDeleteReview(ReviewOperationRequest request, UUID requestUserId) {

    }

    @Override
    public ReviewLikeDto likeReview(ReviewOperationRequest request, UUID requestUserId) {
        return null;
    }
}
