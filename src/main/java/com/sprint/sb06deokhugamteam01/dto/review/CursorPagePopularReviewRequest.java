package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CursorPagePopularReviewRequest(
        RankCriteria period,
        SortDirection direction,
        String cursor,
        LocalDateTime after, // 보조 커서
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
