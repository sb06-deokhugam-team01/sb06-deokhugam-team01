package com.sprint.sb06deokhugamteam01.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.dto.notification.CursorPageResponseNotificationDto;
import com.sprint.sb06deokhugamteam01.exception.common.UnauthorizedAccessException;
import com.sprint.sb06deokhugamteam01.exception.notification.NotificationNotFoundException;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl target;

    private User user;
    @BeforeEach
    void setUp() {
        user = User.builder().build();
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    @DisplayName("updateNotification: 알림을 찾아 확인 상태로 변경한다.")
    void updateNotification_shouldConfirmNotification() {
        UUID notificationId = UUID.randomUUID();
        Notification existing = Notification.builder()
            .id(notificationId)
            .content("old content")
            .confirmed(false)
            .user(user)
            .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));

        Notification result = target.updateNotification(notificationId, user.getId());

        verify(notificationRepository).findById(notificationId);
        assertThat(result.isConfirmed()).isTrue();
    }

    @Test
    @DisplayName("updateNotification: 존재하지 않으면 NotificationNotFoundException을 던진다.")
    void updateNotification_whenNotFound_shouldThrow() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> target.updateNotification(notificationId, user.getId()))
            .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    @DisplayName("updateNotification: 다른 사용자의 알림이면 UnauthorizedAccessException을 던진다.")
    void updateNotification_whenUnauthorized_shouldThrow() {
        UUID notificationId = UUID.randomUUID();
        Notification existing = Notification.builder()
            .id(notificationId)
            .content("old content")
            .confirmed(false)
            .user(User.builder().build()) // 다른 사용자
            .build();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> target.updateNotification(notificationId, user.getId()))
            .isInstanceOf(UnauthorizedAccessException.class);
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
    @DisplayName("getNotifications: 사용자 알림을 DTO로 매핑하고 next 커서를 포함해 반환한다.")
    void getNotifications_shouldReturnCursorPageResponse() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().email("a@a.com").password("p").nickname("n").build();
        ReflectionTestUtils.setField(user, "id", userId);

        Notification first = Notification.builder()
            .id(UUID.randomUUID())
            .user(user)
            .confirmed(false)
            .content("c1")
            .build();
        ReflectionTestUtils.setField(first, "createdAt", LocalDateTime.now());

        Notification second = Notification.builder()
            .id(UUID.randomUUID())
            .user(user)
            .confirmed(true)
            .content("c2")
            .build();
        ReflectionTestUtils.setField(second, "createdAt", LocalDateTime.now().minusSeconds(1));

        SliceImpl<Notification> slice = new SliceImpl<>(List.of(first, second));

        when(notificationRepository.getNotifications(userId, null, null, false, null, null))
            .thenReturn(slice);

        CursorPageResponseNotificationDto result =
            target.getNotifications(userId, "DESC", null, null, null, null);

        verify(notificationRepository).getNotifications(userId, null, null, false, null, null);
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).id()).isEqualTo(first.getId());
        assertThat(result.nextCursor()).isNull(); // hasNext=false
        assertThat(result.hasNext()).isFalse();
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
