package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.domain.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.review.Review;
import com.sprint.sb06deokhugamteam01.domain.review.ReviewSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<Review> getReviews(ReviewSearchCondition condition, Pageable pageable);

    Slice<Review> getPopularReviews(PopularReviewSearchCondition condition, Pageable pageable);

}
