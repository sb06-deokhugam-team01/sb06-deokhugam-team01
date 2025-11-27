package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(String content) {
        return null;
    }

    @Override
    public Notification updateNotification(String notificationId, String newContent, boolean confirmed) {
        return null;
    }

    @Override
    public Notification deleteNotification(String notificationId) {
        return null;
    }

    @Override
    public List<Notification> getNotifications() {
        return null;
    }
}
