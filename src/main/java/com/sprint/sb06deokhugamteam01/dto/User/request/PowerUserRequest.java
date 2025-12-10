package com.sprint.sb06deokhugamteam01.dto.User.request;

import com.sprint.sb06deokhugamteam01.domain.batch.PeriodType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

public record PowerUserRequest(
    String period,
    String direction,
    String cursor,
    LocalDateTime after,
    Integer limit
) {

    @Builder
    public PowerUserRequest(String period, String direction, String cursor, LocalDateTime after, Integer limit) {
        this.period = period != null ? period : "DAILY";
        this.direction = direction != null ? direction : "ASC";
        this.cursor = cursor;
        this.after = after; // null이면 최신 집계일자를 사용한다.
        this.limit = limit != null ? limit : 20;
    }

    public LocalDate setPeriodStart(LocalDate after) {
        return switch (period.toUpperCase()) {
            case "WEEKLY" -> after.minusDays(6);
            case "MONTHLY" -> after.minusDays(29);
            case "DAILY" -> after;
            case "ALL_TIME" -> LocalDate.of(1970, 1, 1); // postgres min date guard
            default -> throw new IllegalArgumentException("Invalid period type: " + period);
        };
    }

    public PeriodType toPeriodType() {
        return PeriodType.valueOf(period.toUpperCase());
    }

}
