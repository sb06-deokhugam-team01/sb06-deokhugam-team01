package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.dto.notification.CursorPageResponseNotificationDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Notification updateNotification(UUID notificationId, UUID userId, boolean status);

    Notification deleteNotification(UUID notificationId);

    CursorPageResponseNotificationDto getNotifications(
        UUID userId,
        String direction,
        String cursor,
        LocalDateTime after,
        Integer limit,
        Pageable pageable
    );

    List<Notification> updateAll(UUID userId);
}
