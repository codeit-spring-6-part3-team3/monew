package com.team03.monew.notification.scheduler;

import com.team03.monew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // 매 04시 만료 알림 삭제 (비동기)
    @Async
    @Scheduled(cron = "0 0 4 * * *")
    public void deleteExpiredNotifications() {
        log.info("[알림] 삭제 : 시작");

        try {
            LocalDateTime expiredDate = LocalDateTime.now().minusWeeks(1);
            int deletedCount = notificationService.deleteExpiredNotifications(expiredDate);
            log.info("[알림] 삭제 : {} 건 완료", deletedCount);
        } catch (Exception e) {
            log.error("[알림] 삭제 : 실패", e);
        }
    }
}