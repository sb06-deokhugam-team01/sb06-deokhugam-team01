package com.sprint.sb06deokhugamteam01.dto.review;

import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CursorPagePopularReviewRequest(

        RankCriteria period,
        SortDirection direction,
        String cursor,
        LocalDateTime after, // 보조 커서

        @Min(value = 1)
        Integer limit
) {
    public enum RankCriteria {
        DAILY,
        WEEKLY,
        MONTHLY,
        ALL_TIME
    }

    public enum SortDirection {
        ASC,
        DESC
    }
}
