package com.team03.monew.web.controller;

import com.team03.monew.notification.dto.CursorPageResponseNotificationDto;
import com.team03.monew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> getUncheckedNotifications(
            @RequestParam(required = false) String cursor,
            @RequestParam() String after,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam() UUID userId
    ) {
        if (cursor == null) {
            return ResponseEntity.ok(notificationService.getUncheckedNotifications(userId, limit));
        }
        return ResponseEntity.ok(notificationService.getUncheckedNotificationsWithCursor(userId, cursor, limit));
    }

    @PatchMapping
    public ResponseEntity<Void> markAllAsChecked(
            @RequestParam() UUID userId
    ) {
        notificationService.markAllAsChecked(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> markAsChecked(
            @PathVariable() UUID notificationId,
            @RequestParam() UUID userId
    ) {
        notificationService.markAsChecked(notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
