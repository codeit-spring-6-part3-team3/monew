package com.team03.monew.articleCollect.event;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.notification.domain.NoticeResourceType;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticlesNotificationBatchAggregator {

  private final InterestRepository interestRepository;
  private final SubscribeRepository subscribeRepository;
  private final ApplicationEventPublisher publisher;

  // 관심사별 뉴스 건수 버퍼
  private final Map<UUID, Integer> interestCountBuffer = new HashMap<>();
  private final Object lock = new Object();
  private Instant lastFlush = Instant.now();

  private static final int MAX_BATCH_SIZE = 10;
  private static final long MAX_WAIT_MS = 3_000L;

  @EventListener
  @Async
  public void onArticlesCollected(ArticlesCollectedEvent event) {
    synchronized (lock) {
      event.interestIds().forEach(id ->
          interestCountBuffer.merge(id, 1, Integer::sum)
      );
    }
  }

  @Scheduled(fixedDelay = 1_000)
  public void checkFlush() {
    Map<UUID, Integer> snapshot;
    String reason;

    synchronized (lock) {
      if (interestCountBuffer.isEmpty()) {
        return;
      }

      long elapsed = Duration.between(lastFlush, Instant.now()).toMillis();
      boolean timeFlush = elapsed >= MAX_WAIT_MS;
      int totalCount = interestCountBuffer.values().stream().mapToInt(Integer::intValue).sum();
      boolean sizeFlush = totalCount >= MAX_BATCH_SIZE;

      if (!timeFlush && !sizeFlush) {
        return;
      }

      snapshot = new HashMap<>(interestCountBuffer);
      interestCountBuffer.clear();
      lastFlush = Instant.now();
      reason = timeFlush ? "time>=3s" : "size>=10";
    }

    log.info("ArticlesNotificationBatchAggregator flush 시작: reason={}, interests={}, total={}",
        reason, snapshot.size(), snapshot.values().stream().mapToInt(Integer::intValue).sum());

    // 관심사 이름을 배치 조회해 맵 구성
    Map<UUID, String> nameMap = interestRepository.findAllById(snapshot.keySet()).stream()
        .collect(Collectors.toMap(Interest::getId, Interest::getName));

    // 구독자 배치 조회 후 interestId별로 그룹핑
    Map<UUID, List<UUID>> subscribersByInterest = subscribeRepository.findByInterestIdIn(
            snapshot.keySet().stream().toList())
        .stream()
        .collect(Collectors.groupingBy(
            Subscribe::getInterestId,
            Collectors.mapping(Subscribe::getUserId, Collectors.toList())
        ));

    snapshot.forEach((interestId, count) -> processInterest(
        interestId,
        count,
        nameMap.getOrDefault(interestId, "관심사"),
        subscribersByInterest.getOrDefault(interestId, List.of())
    ));

    log.info("ArticlesNotificationBatchAggregator flush 완료");
  }

  /**
   * 관심사 하나에 대한 알림 DTO를 생성해 이벤트로 발행.
   */
  private void processInterest(UUID interestId, int count, String interestName, List<UUID> userIds) {
    if (count <= 0) {
      return;
    }

    String context = "%s에 대한 새 기사 %d건이 있어요.".formatted(interestName, count);

    if (userIds.isEmpty()) {
      log.info("관심사 {} 구독자 없음 → 알림 생략", interestId);
      return;
    }

    // 각 사용자에게 보낼 알림 DTO 이벤트 발행
    userIds.forEach(userId -> {
      NotificationCreateDto dto = new NotificationCreateDto(
          userId,
          context,
          NoticeResourceType.INTEREST,
          interestId
      );
      publisher.publishEvent(dto);
    });

    log.info("관심사 {} 알림 {}건 발행 완료 (count={})", interestId, userIds.size(), count);
  }
}
