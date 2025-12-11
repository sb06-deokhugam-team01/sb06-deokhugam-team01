package com.sprint.sb06deokhugamteam01.repository.notification;

import com.sprint.sb06deokhugamteam01.domain.Notification;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationRepositoryCustom {

    Slice<Notification> getNotifications(
        UUID userId,
        String cursor,
        LocalDateTime after,
        boolean ascending,
        Integer limit,
        Pageable pageable
    );
}
