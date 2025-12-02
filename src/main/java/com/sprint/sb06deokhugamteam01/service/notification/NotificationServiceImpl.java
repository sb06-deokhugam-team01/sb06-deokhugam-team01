package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.exception.notification.NotificationNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification updateNotification(UUID notificationId, String newContent,
        boolean confirmed) {
        Notification optionalNotification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(Map.of("notificationId", notificationId)));

        optionalNotification.confirm();

        return optionalNotification;
    }

    @Override
    @Transactional
    public Notification deleteNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(Map.of("notificationId", notificationId)));
        notificationRepository.delete(notification);
        return notification;
    }

    @Override
    public Slice<Notification> getNotifications(UUID userId, String direction, String cursor,
        LocalDateTime after, Integer limit, Pageable pageable) {
        boolean ascending = direction != null && direction.equalsIgnoreCase("ASC");
        return notificationRepository.getNotifications(userId, cursor, after, ascending, limit, pageable);
    }

    @Override
    @Transactional
    public List<Notification> updateAll(UUID userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdAndConfirmedFalse(userId);
        notifications.forEach(Notification::confirm);
        return notificationRepository.saveAll(notifications);
    }
}
