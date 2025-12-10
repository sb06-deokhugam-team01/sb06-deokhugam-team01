package com.sprint.sb06deokhugamteam01.dto.review;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopularReviewSearchCondition(
        PeriodType period,
        boolean descending,
        String cursor,
        LocalDateTime after,
        int limit
) {
}
