package com.team03.monew.notification.repository;

import com.team03.monew.notification.domain.Notification;
import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationRepositoryCustom {

    // 커서 페이지네이션, 알림 조회, QueryDSL 사용
    Slice<Notification> findNotificationsWithCursor(
            UUID userId,
            String cursor,
            int size,
            LocalDateTime after
    );

    // 알림 조회 (첫 페이지 용도), QueryDSL 사용
    Slice<Notification> findNotifications(
            UUID userId,
            int size,
            LocalDateTime after
    );
}
