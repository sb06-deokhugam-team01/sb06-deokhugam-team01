package com.sprint.sb06deokhugamteam01.dto.review.request;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;

@Builder
public record CursorPagePopularReviewRequest(

        @Nullable
        PeriodType period,

        @Nullable
        SortDirection direction,

        @Nullable
        String cursor,

        @Nullable
        LocalDateTime after, // 보조 커서

        @Nullable
        @Min(value = 1)
        Integer limit
) {
    public enum SortDirection {
        ASC,
        DESC
    }
}
