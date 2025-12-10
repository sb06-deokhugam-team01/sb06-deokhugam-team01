package com.sprint.sb06deokhugamteam01.service.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.dto.notification.CursorPageResponseNotificationDto;
import com.sprint.sb06deokhugamteam01.dto.notification.NotificationDto;
import com.sprint.sb06deokhugamteam01.exception.common.UnauthorizedAccessException;
import com.sprint.sb06deokhugamteam01.exception.notification.NotificationNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public Notification updateNotification(UUID notificationId, UUID userId, boolean confirmed) {
        Notification optionalNotification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(Map.of("notificationId", notificationId)));

        if(optionalNotification.getUser() == null ||
            !Objects.equals(optionalNotification.getUser().getId(), userId)){
            throw new UnauthorizedAccessException(Map.of("userId", userId, "notificationId", notificationId));
        }

        optionalNotification.changeConfirm(confirmed);

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
    public CursorPageResponseNotificationDto getNotifications(UUID userId, String direction,
        String cursor, LocalDateTime after, Integer limit, Pageable pageable) {
        boolean ascending = direction != null && direction.equalsIgnoreCase("ASC");
        Slice<Notification> slice =
            notificationRepository.getNotifications(userId, cursor, after, ascending, limit, pageable);

        List<NotificationDto> content = slice.getContent().stream()
            .map(NotificationDto::fromEntity)
            .toList();

        Notification last = slice.hasContent() ? slice.getContent().get(slice.getContent().size() - 1) : null;
        String nextCursor = slice.hasNext() && last != null ? last.getId().toString() : null;
        LocalDateTime nextAfter = slice.hasNext() && last != null ? last.getCreatedAt() : null;

        return new CursorPageResponseNotificationDto(
            content,
            nextCursor,
            nextAfter,
            content.size(),
            (long) content.size(),
            slice.hasNext()
        );
    }

    @Override
    @Transactional
    public List<Notification> updateAll(UUID userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdAndConfirmedFalse(userId);
        notifications.forEach(Notification::confirm);
        return notificationRepository.saveAll(notifications);
    }
}
