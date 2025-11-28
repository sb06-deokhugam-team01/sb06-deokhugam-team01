package com.sprint.sb06deokhugamteam01.domain.review;

import com.sprint.sb06deokhugamteam01.dto.review.CursorPagePopularReviewRequest;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopularReviewSearchCondition(
        CursorPagePopularReviewRequest.RankCriteria period,
        boolean descending,
        String cursor,
        LocalDateTime after,
        Integer limit
) {
}
