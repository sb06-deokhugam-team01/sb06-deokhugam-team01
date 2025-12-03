package com.sprint.sb06deokhugamteam01.controller;

import com.sprint.sb06deokhugamteam01.dto.notification.CursorPageResponseNotificationDto;
import com.sprint.sb06deokhugamteam01.service.notification.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
        @RequestParam UUID userId,
        @RequestParam(required = false, defaultValue = "DESC") String direction,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
        @RequestParam(required = false) Integer limit
    ) {
        log.info("Notifications request: userId={}, direction={}, cursor={}, after={}, limit={}", userId, direction, cursor, after, limit);
        return ResponseEntity.ok(notificationService.getNotifications(userId, direction, cursor, after, limit, null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        log.info("Mark all notifications as read request: userId={}", userId);
        notificationService.updateAll(userId);
        return ResponseEntity.ok().build();
    }
}
