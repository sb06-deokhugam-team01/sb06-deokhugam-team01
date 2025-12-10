package com.sprint.sb06deokhugamteam01.dto.notification;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseNotificationDto(
    List<NotificationDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) {

}
