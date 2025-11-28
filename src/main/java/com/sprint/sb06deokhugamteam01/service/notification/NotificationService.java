package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification updateNotification(UUID notificationId, String newContent, boolean confirmed);

    Notification deleteNotification(UUID notificationId);

    List<Notification> getNotifications(UUID userId);

    List<Notification> updateAll(UUID userId);
}
