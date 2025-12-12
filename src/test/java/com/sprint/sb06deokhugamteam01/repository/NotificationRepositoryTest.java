package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.config.QueryDslConfig;
import com.sprint.sb06deokhugamteam01.domain.Notification;
import com.sprint.sb06deokhugamteam01.domain.User;
import com.sprint.sb06deokhugamteam01.repository.notification.NotificationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
                NotificationRepository.class, QueryDslConfig.class
        }
))
@ActiveProfiles("test")
public class NotificationRepositoryTest {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EntityManager em;


    private void forceUpdateCreatedAt(UUID id, LocalDateTime time){
        Query query = em.createNativeQuery("UPDATE notification SET created_at=?1 WHERE id=?2");
        query.setParameter(1, time);
        query.setParameter(2, id);
        query.executeUpdate();
    }

    @Test
    @DisplayName("getNotifications 성공")
    void getNotifications_Success() {
        // given
        User user = User.builder().build();
        em.persist(user);
        LocalDateTime time = LocalDateTime.now();
        boolean ascending = true;
        Integer limit = 10;
        Pageable pageable = null;

        Notification notification = notificationRepository.save(new Notification(null, "content", false,
                null, null, user, null));
        forceUpdateCreatedAt(notification.getId(), time);
        Notification notification2 = notificationRepository.save(new Notification(null, "content", true,
                null, null, user, null));
        forceUpdateCreatedAt(notification2.getId(), time.minusDays(1));
        Notification notification3 = notificationRepository.save(new Notification(null, "content", true,
                null, null, user, null));
        forceUpdateCreatedAt(notification3.getId(), time.minusDays(2));
        em.refresh(notification);
        em.refresh(notification2);
        em.refresh(notification3);

        // when
        Slice<Notification> result = notificationRepository.getNotifications(user.getId(), notification2.getId().toString(),
                notification2.getCreatedAt(), ascending, limit, pageable);

        // then
        assertThat(notificationRepository.count()).isEqualTo(3);
        assertThat(result).hasSize(1);
        assertThat(result).extracting(Notification::getId).containsExactly(notification.getId());
    }
}
