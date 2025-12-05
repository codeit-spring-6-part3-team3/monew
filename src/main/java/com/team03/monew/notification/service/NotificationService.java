package com.team03.monew.notification.service;

import com.team03.monew.notification.dto.CursorPageResponseNotificationDto;
import com.team03.monew.notification.dto.NotificationCreateDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationService {

    // 알림 생성 (댓글 좋아요, 뉴스 등재 등에서 호출)
    void createNotification(NotificationCreateDto createDto);

    // 미확인 알림을 커서 기반 페이지네이션으로 조회 (첫 페이지)
    CursorPageResponseNotificationDto getUncheckedNotifications(
            UUID userId,
            int size
    );

    // 미확인 알림을 커서 기반 페이지네이션으로 조회 (다음 페이지)
    CursorPageResponseNotificationDto getUncheckedNotificationsWithCursor(
            UUID userId,
            String cursor,
            int size
    );

    int deleteExpiredNotifications(LocalDateTime expiredDate);

    // 모든 미확인 알림 일괄 확인 처리
    void markAllAsChecked(UUID userId);

    // 알림 확인 처리
    void markAsChecked(UUID notificationId, UUID userId);
}
