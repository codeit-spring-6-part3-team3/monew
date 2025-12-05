package com.team03.monew.notification.service;

import com.team03.monew.notification.dto.CursorPageResponseNotificationDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationService {

    // 미확인 알림을 커서 기반 페이지네이션으로 조회 (첫 페이지)
    CursorPageResponseNotificationDto getUncheckedNotifications(
            UUID userId,
            int size
    );

    // 미확인 알림을 커서 기반 페이지네이션으로 조회 (다음 페이지)
    CursorPageResponseNotificationDto getUncheckedNotificationsWithCursor(
            UUID userId,
            LocalDateTime cursor,
            int size
    );

    // 알림 확인 처리
    void markAsChecked(Long notificationId, UUID userId);

    // 모든 미확인 알림 일괄 확인 처리
    void markAllAsChecked(UUID userId);

}