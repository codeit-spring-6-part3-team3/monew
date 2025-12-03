package com.team03.monew.notification.service;

import com.team03.monew.notification.domain.Notification;
import com.team03.monew.notification.dto.CursorPageResponseNotificationDto;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.notification.dto.NotificationDto;
import com.team03.monew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createNotification(NotificationCreateDto createDto) {
        Notification notification = Notification.from(
                createDto.userId(),
                createDto.context(),
                createDto.resource(),
                createDto.resourceId()
        );
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseNotificationDto getUncheckedNotifications(UUID userId, int size) {
        Slice<Notification> slice = notificationRepository.findNotifications(userId, size);
        return CursorPageResponseNotificationDto.from(slice, size);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseNotificationDto getUncheckedNotificationsWithCursor(
            UUID userId,
            String cursor,
            int size
    ) {
        Slice<Notification> slice = notificationRepository.findNotificationsWithCursor(
                userId,
                cursor,
                size
        );
        return CursorPageResponseNotificationDto.from(slice, size);
    }

    @Override
    @Transactional
    public int deleteExpiredNotifications(LocalDateTime expiredDate) {
        return notificationRepository.deleteExpiredNotifications(expiredDate);
    }

    @Override
    @Transactional
    public void markAsChecked(Long notificationId, UUID userId) {
    }

    @Override
    @Transactional
    public void markAllAsChecked(UUID userId) {
    }

}
