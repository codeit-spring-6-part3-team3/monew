package com.team03.monew.notification.event;

import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림 생성 이벤트를 수신해 DB 저장 및 후속 처리를 수행한다.
 * AFTER_COMMIT으로 커밋 이후에만 동작하므로 롤백 시 중복 처리를 막는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

  private final NotificationService notificationService;

  @PostConstruct
  void init() {
    log.info("NotificationEventHandler initialized");
  }

  @Async
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT,
      fallbackExecution = true // 트랜잭션이 없을 때도 실행되도록 허용
  )
  public void handleNotification(NotificationCreateDto event) {
    log.debug("NotificationEventHandler received: userId={} resource={} resourceId={}",
        event.userId(), event.resource(), event.resourceId());
    notificationService.createNotification(event);
  }
}
