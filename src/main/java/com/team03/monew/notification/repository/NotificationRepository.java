package com.team03.monew.notification.repository;

import com.team03.monew.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, NotificationRepositoryCustom {

    // 만료 알림 삭제 (배치 처리)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isChecked = true AND n.updatedAt < :expiredDate")
    int deleteExpiredNotifications(
            @Param("expiredDate") LocalDateTime expiredDate
    );

    // 미확인 알림 한 번에 확인 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isChecked = true WHERE n.userId = :userId AND n.isChecked = false")
    int markAllAsChecked(
            @Param("userId") UUID userId
    );

    // 미확인 알림 한 번에 확인 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isChecked = true WHERE n.userId = :userId AND n.id = :id AND n.isChecked = false")
    int markAsChecked(
            @Param("id") UUID id,
            @Param("userId") UUID userId
    );

    Long countByUserId(UUID userId);
}
