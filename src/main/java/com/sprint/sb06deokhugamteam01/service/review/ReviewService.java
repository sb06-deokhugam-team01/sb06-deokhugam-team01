package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.dto.review.*;

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

    ReviewLikeDto likeReview(UUID reviewId, UUID requestUserId);

}
