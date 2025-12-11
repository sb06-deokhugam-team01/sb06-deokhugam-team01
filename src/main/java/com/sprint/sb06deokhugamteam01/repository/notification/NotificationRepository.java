package com.sprint.sb06deokhugamteam01.repository.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, UUID>,
    NotificationRepositoryCustom {

    List<Notification> findAllByUserIdAndConfirmedFalse(UUID userId);

    @Modifying
    void deleteByConfirmedIsTrueAndUpdatedAtBefore(LocalDateTime threshold);
}
