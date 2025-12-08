package com.sprint.sb06deokhugamteam01.dto.review.request;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CursorPagePopularReviewRequest(

        PeriodType period,
        SortDirection direction,
        String cursor,
        LocalDateTime after, // 보조 커서

        @Min(value = 1)
        Integer limit
) {
    public enum SortDirection {
        ASC,
        DESC
    }
}
