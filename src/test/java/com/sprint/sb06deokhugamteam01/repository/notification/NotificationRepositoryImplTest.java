package com.sprint.sb06deokhugamteam01.repository.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@Import(QueryDslConfig.class)
class NotificationRepositoryImplTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2025, 1, 1, 12, 0);
    private static final Comparator<Notification> DESC_COMPARATOR = Comparator
        .comparing(Notification::getCreatedAt)
        .reversed()
        .thenComparing(Notification::getId, Comparator.reverseOrder());

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("getNotifications: createdAt DESC 기본 정렬로 limit+1 조회한다.")
    void getNotifications_shouldReturnSliceDescending() {
        User user = saveUser();
        Notification newest = saveNotification(user, "n1", BASE_TIME);
        Notification middle = saveNotification(user, "n2", BASE_TIME.minusMinutes(1));
        Notification oldest = saveNotification(user, "n3", BASE_TIME.minusMinutes(2));
        entityManager.flush();
        entityManager.clear();

        List<Notification> sorted = sortByRepositoryOrder(newest, middle, oldest);

        Slice<Notification> result = notificationRepository.getNotifications(
            user.getId(), null, null, false, 2, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent())
            .isSortedAccordingTo(DESC_COMPARATOR);
        assertThat(result.getContent())
            .extracting(Notification::getId)
            .containsExactly(
                sorted.get(0).getId(),
                sorted.get(1).getId()
            );
    }

    @Test
    @DisplayName("getNotifications: cursor와 after(createdAt)로 다음 페이지를 조회한다.")
    void getNotifications_withCursor_shouldReturnNextPage() {
        User user = saveUser();
        Notification newest = saveNotification(user, "n1", BASE_TIME);
        Notification middle = saveNotification(user, "n2", BASE_TIME.minusMinutes(1));
        Notification oldest = saveNotification(user, "n3", BASE_TIME.minusMinutes(2));
        entityManager.flush();
        entityManager.clear();

        List<Notification> sorted = sortByRepositoryOrder(newest, middle, oldest);

        Slice<Notification> firstPage = notificationRepository.getNotifications(
            user.getId(), null, null, false, 2, PageRequest.of(0, 2));

        Notification lastOfFirstPage = firstPage.getContent().get(1);
        Slice<Notification> secondPage = notificationRepository.getNotifications(
            user.getId(),
            lastOfFirstPage.getId().toString(),
            lastOfFirstPage.getCreatedAt(),
            false,
            2,
            PageRequest.of(0, 2)
        );

        assertThat(secondPage.getContent())
            .extracting(Notification::getId)
            .containsExactly(sorted.get(2).getId());
        assertThat(secondPage.hasNext()).isFalse();
        assertThat(firstPage.getContent())
            .extracting(Notification::getId)
            .containsExactly(
                sorted.get(0).getId(),
                sorted.get(1).getId()
            );
    }

    private List<Notification> sortByRepositoryOrder(Notification... notifications) {
        return Arrays.stream(notifications)
            .sorted(DESC_COMPARATOR)
            .toList();
    }

    private User saveUser() {
        User user = User.builder()
            .email("test@example.com")
            .nickname("tester")
            .password("pwd")
            .build();
        ReflectionTestUtils.setField(user, "isActive", true);
        entityManager.persist(user);
        return user;
    }

    private Notification saveNotification(User user, String content, LocalDateTime createdAt) {
        Notification notification = Notification.builder()
            .user(user)
            .content(content)
            .confirmed(false)
            .build();
        ReflectionTestUtils.setField(notification, "createdAt", createdAt);
        ReflectionTestUtils.setField(notification, "updatedAt", createdAt);
        entityManager.persist(notification);
        return notification;
    }
}
