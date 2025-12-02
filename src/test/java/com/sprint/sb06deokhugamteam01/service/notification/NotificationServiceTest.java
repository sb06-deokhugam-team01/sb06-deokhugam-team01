package com.sprint.sb06deokhugamteam01.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.exception.notification.NotificationNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl target;

    @Test
    @DisplayName("updateNotification: 알림을 찾아 확인 상태로 변경한다.")
    void updateNotification_shouldConfirmNotification() {
        UUID notificationId = UUID.randomUUID();
        Notification existing = Notification.builder()
            .id(notificationId)
            .content("old content")
            .confirmed(false)
            .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));

        Notification result = target.updateNotification(notificationId, "new content", true);

        verify(notificationRepository).findById(notificationId);
        assertThat(result.isConfirmed()).isTrue();
    }

    @Test
    @DisplayName("updateNotification: 존재하지 않으면 NotificationNotFoundException을 던진다.")
    void updateNotification_whenNotFound_shouldThrow() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> target.updateNotification(notificationId, "content", true))
            .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    @DisplayName("deleteNotification: 알림을 삭제하고 반환한다.")
    void deleteNotification_shouldRemoveNotification() {
        UUID notificationId = UUID.randomUUID();
        Notification existing = Notification.builder()
            .id(notificationId)
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
    @DisplayName("deleteNotification: 존재하지 않으면 NotificationNotFoundException을 던진다.")
    void deleteNotification_whenNotFound_shouldThrow() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> target.deleteNotification(notificationId))
            .isInstanceOf(NotificationNotFoundException.class);

        verify(notificationRepository, never()).delete(any(Notification.class));
    }

    @Test
    @DisplayName("getNotifications: 사용자별 알림을 정렬/커서 조건으로 조회한다.")
    void getNotifications_shouldDelegateToRepositoryWithDirectionAndCursor() {
        UUID userId = UUID.randomUUID();
        List<Notification> notifications = List.of(
            Notification.builder().id(UUID.randomUUID()).confirmed(false).build(),
            Notification.builder().id(UUID.randomUUID()).confirmed(true).build()
        );
        Slice<Notification> slice = new SliceImpl<>(notifications);

        when(notificationRepository.getNotifications(userId, null, null, false, null, null))
            .thenReturn(slice);

        Slice<Notification> result = target.getNotifications(userId, "DESC", null, null, null, null);

        verify(notificationRepository).getNotifications(userId, null, null, false, null, null);
        assertThat(result.getContent()).isEqualTo(notifications);
    }

    @Test
    @DisplayName("updateAll: 특정 사용자의 미확인 알림을 모두 확인 처리하고 저장한다.")
    void updateAll_shouldConfirmAllAndPersist() {
        UUID userId = UUID.randomUUID();
        Notification first = Notification.builder().id(UUID.randomUUID()).confirmed(false).build();
        Notification second = Notification.builder().id(UUID.randomUUID()).confirmed(false).build();
        List<Notification> notifications = List.of(first, second);
        List<Notification> saved = List.of(
            Notification.builder().id(first.getId()).confirmed(true).build(),
            Notification.builder().id(second.getId()).confirmed(true).build()
        );

        when(notificationRepository.findAllByUserIdAndConfirmedFalse(userId)).thenReturn(notifications);
        when(notificationRepository.saveAll(any())).thenReturn(saved);

        List<Notification> result = target.updateAll(userId);

        verify(notificationRepository).findAllByUserIdAndConfirmedFalse(userId);
        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(captor.capture());

        List<Notification> toSave = captor.getValue();
        assertThat(toSave).hasSize(2);
        assertThat(toSave.get(0).isConfirmed()).isTrue();
        assertThat(toSave.get(1).isConfirmed()).isTrue();
        assertThat(result).isEqualTo(saved);
    }
}
