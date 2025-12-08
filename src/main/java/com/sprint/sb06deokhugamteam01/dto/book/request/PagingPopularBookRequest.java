package com.sprint.sb06deokhugamteam01.dto.book.request;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Builder
public record PagingPopularBookRequest(
        @NotNull(message = "period는 비어 있을 수 없습니다.")
        PeriodType period,
        @NotNull(message = "direction는 비어 있을 수 없습니다.")
        SortDirection direction,
        @Nullable
        @RequestParam(required = false)
        String cursor,
        @Nullable
        @RequestParam(required = false)
        LocalDateTime after,
        @Min(value = 1, message = "limit는 최소 1 이상이어야 합니다.")
        Integer limit

) {

    public enum SortDirection {
        ASC,
        DESC
    }

}
