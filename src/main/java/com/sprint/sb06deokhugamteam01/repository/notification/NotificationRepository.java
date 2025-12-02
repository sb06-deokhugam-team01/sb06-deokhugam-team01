package com.sprint.sb06deokhugamteam01.repository.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID>,
    NotificationRepositoryCustom {

    List<Notification> findAllByUserIdAndConfirmedFalse(UUID userId);
}
