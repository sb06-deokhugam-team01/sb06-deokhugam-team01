package com.sprint.sb06deokhugamteam01.dto.review;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CursorPagePopularReviewRequest(
        RankCriteria period,
        String direction,
        String cursor,
        LocalDateTime after, // 보조 커서
        int limit
) {
    public enum RankCriteria {
        DAILY, WEEKLY, MONTHLY, ALL_TIME
    }
}
