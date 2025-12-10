package com.sprint.sb06deokhugamteam01.dto.notification;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public record PageNotificationRequest(
        UUID userId,
        String direction,
        String cursor,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
        Integer limit
) {
    public boolean isAscending() {
        return direction != null && direction.equalsIgnoreCase("ASC");
    }
}
