package com.sprint.sb06deokhugamteam01.repository.review;

import com.sprint.sb06deokhugamteam01.dto.review.PopularReviewSearchCondition;
import com.sprint.sb06deokhugamteam01.domain.Review;
import com.sprint.sb06deokhugamteam01.dto.review.ReviewSearchCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public interface ReviewRepositoryCustom {

    Slice<Review> getReviews(ReviewSearchCondition condition, Pageable pageable);

    Slice<Review> getPopularReviews(PopularReviewSearchCondition condition, Pageable pageable);

    void deleteByBookId(UUID bookId);

}
