package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import java.util.List;

public interface NotificationService {

    Notification createNotification(String content);

    Notification updateNotification(String notificationId, String newContent, boolean confirmed);

    Notification deleteNotification(String notificationId);

    List<Notification> getNotifications();
}
