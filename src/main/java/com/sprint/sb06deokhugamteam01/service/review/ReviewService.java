package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPagePopularReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.CursorPageReviewRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewCreateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.request.ReviewUpdateRequest;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponsePopularReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.CursorPageResponseReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewDto;
import com.sprint.sb06deokhugamteam01.dto.review.response.ReviewLikeDto;

import java.util.UUID;

public interface ReviewService {

    ReviewDto createReview(ReviewCreateRequest request);

    ReviewDto getReview(UUID reviewId, UUID requestUserId);

    CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request, UUID requestUserId);

    CursorPageResponsePopularReviewDto getPopularReviews(CursorPagePopularReviewRequest request,
                                                         UUID requestUserId);

    ReviewDto updateReview(UUID reviewId,
                           ReviewUpdateRequest updateRequest,
                           UUID requestUserId);

    void deleteReview(UUID reviewId, UUID requestUserId);

    void hardDeleteReview(UUID reviewId, UUID requestUserId);

    ReviewLikeDto likeReviewToggle(UUID reviewId, UUID requestUserId);

}
