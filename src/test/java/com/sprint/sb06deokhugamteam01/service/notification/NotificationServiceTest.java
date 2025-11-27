package com.sprint.sb06deokhugamteam01.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.repository.NotificationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl target;

    @Test
    void createNotification_shouldPersistNewNotification() {
        String content = "new notification";
        Notification saved = Notification.builder()
            .id(UUID.randomUUID())
            .content(content)
            .confirmed(false)
            .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        Notification result = target.createNotification(content);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification toSave = captor.getValue();

        assertThat(getContent(toSave)).isEqualTo(content);
        assertThat(isConfirmed(toSave)).isFalse();
        assertThat(result).isEqualTo(saved);
    }

    @Test
    void updateNotification_shouldModifyContentAndConfirmation() {
        String notificationId = UUID.randomUUID().toString();
        Notification existing = Notification.builder()
            .id(UUID.fromString(notificationId))
            .content("old content")
            .confirmed(false)
            .build();
        Notification updated = Notification.builder()
            .id(UUID.fromString(notificationId))
            .content("updated content")
            .confirmed(true)
            .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updated);

        Notification result = target.updateNotification(notificationId, "updated content", true);

        verify(notificationRepository).findById(notificationId);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification toSave = captor.getValue();

        assertThat(getContent(toSave)).isEqualTo("updated content");
        assertThat(isConfirmed(toSave)).isTrue();
        assertThat(result).isEqualTo(updated);
    }

    @Test
    void deleteNotification_shouldRemoveNotification() {
        String notificationId = UUID.randomUUID().toString();
        Notification existing = Notification.builder()
            .id(UUID.fromString(notificationId))
            .content("delete me")
            .confirmed(false)
            .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));

        Notification result = target.deleteNotification(notificationId);

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).delete(existing);
        assertThat(result).isEqualTo(existing);
    }

    @Test
    void getNotifications_shouldReturnAllNotifications() {
        List<Notification> notifications = List.of(
            Notification.builder().id(UUID.randomUUID()).content("first").confirmed(false).build(),
            Notification.builder().id(UUID.randomUUID()).content("second").confirmed(true).build()
        );
        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = target.getNotifications();

        verify(notificationRepository).findAll();
        assertThat(result).isEqualTo(notifications);
    }

    private String getContent(Notification notification) {
        return (String) ReflectionTestUtils.getField(notification, "content");
    }

    private boolean isConfirmed(Notification notification) {
        Boolean confirmed = (Boolean) ReflectionTestUtils.getField(notification, "confirmed");
        return Boolean.TRUE.equals(confirmed);
    }
}
