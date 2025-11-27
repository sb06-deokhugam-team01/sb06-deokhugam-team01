package com.sprint.sb06deokhugamteam01.service.review;

import com.sprint.sb06deokhugamteam01.dto.review.*;

import java.util.UUID;

public interface ReviewService {

    ReviewDto createReview(ReviewCreateRequest request);

    ReviewDto getReview(ReviewOperationRequest request, UUID requestUserId);

    CursorPageResponseReviewDto getReviews(CursorPageReviewRequest request);

    CursorPageResponsePopularReviewDto getPopularReviews(CursorPagePopularReviewRequest request);

    ReviewDto updateReview(ReviewOperationRequest request,
                           ReviewUpdateRequest updateRequest,
                           UUID requestUserId);

    void deleteReview(ReviewOperationRequest request, UUID requestUserId);

    void hardDeleteReview(ReviewOperationRequest request, UUID requestUserId);

    ReviewLikeDto likeReview(ReviewOperationRequest request, UUID requestUserId);

}
