package com.team03.monew.articleCollect.event;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticlesNotificationBatchAggregatorTest {

  @Mock
  InterestRepository interestRepository;
  @Mock
  SubscribeRepository subscribeRepository;
  @Mock
  ApplicationEventPublisher publisher;

  @InjectMocks
  ArticlesNotificationBatchAggregator aggregator;

  @Test
  @DisplayName("버퍼 크기가 임계값 이상이면 즉시 flush하여 알림 이벤트를 발행한다")
  void flushBySize() {
    // given: 동일 관심사에 대한 10건을 적재
    UUID interestId = UUID.randomUUID();
    for (int i = 0; i < 10; i++) {
      aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestId)));
    }

    Interest interest = Interest.builder().name("테스트").keywords(List.of("k")).build();
    ReflectionTestUtils.setField(interest, "id", interestId);
    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of(interest));
    UUID userA = UUID.randomUUID();
    UUID userB = UUID.randomUUID();
    when(subscribeRepository.findByInterestIdIn(List.of(interestId)))
        .thenReturn(List.of(
            Subscribe.builder().userId(userA).interestId(interestId).build(),
            Subscribe.builder().userId(userB).interestId(interestId).build()
        ));

    ArgumentCaptor<NotificationCreateDto> captor = ArgumentCaptor.forClass(NotificationCreateDto.class);

    // when
    aggregator.checkFlush();

    // then: count=10을 포함한 컨텍스트로 사용자 수(2) 만큼 발행
    verify(publisher, times(2)).publishEvent(captor.capture());
    List<NotificationCreateDto> events = captor.getAllValues();
    assertThat(events).allMatch(dto -> dto.context().contains("10건"));
  }

  @Test
  @DisplayName("시간 조건이 충족되면 버퍼 크기와 상관없이 flush한다")
  void flushByTime() {
    UUID interestId = UUID.randomUUID();
    aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestId)));

    // lastFlush를 과거로 설정해 시간 조건 만족시킴
    ReflectionTestUtils.setField(aggregator, "lastFlush", Instant.now().minusMillis(4_000));

    Interest interest = Interest.builder().name("시사").keywords(List.of("a")).build();
    ReflectionTestUtils.setField(interest, "id", interestId);
    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of(interest));
    when(subscribeRepository.findByInterestIdIn(List.of(interestId)))
        .thenReturn(List.of(Subscribe.builder().userId(UUID.randomUUID()).interestId(interestId).build()));

    // when
    aggregator.checkFlush();

    // then
    ArgumentCaptor<NotificationCreateDto> captor = ArgumentCaptor.forClass(NotificationCreateDto.class);
    verify(publisher, times(1)).publishEvent(captor.capture());
    NotificationCreateDto dto = captor.getValue();
    assertThat(dto.resourceId()).isEqualTo(interestId);
  }

  @Test
  @DisplayName("구독자가 없으면 알림을 발행하지 않는다")
  void skipWhenNoSubscribers() {
    UUID interestId = UUID.randomUUID();
    aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestId)));

    // 시간 조건을 만족시켜 flush 실행
    ReflectionTestUtils.setField(aggregator, "lastFlush", Instant.now().minusMillis(4_000));

    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of());
    when(subscribeRepository.findByInterestIdIn(List.of(interestId))).thenReturn(List.of());

    aggregator.checkFlush();

    verify(publisher, never()).publishEvent(any());
  }

  @Test
  @DisplayName("여러 관심사에 대한 누적 건수를 각 관심사별로 나눠서 발행한다")
  void flushMultipleInterests() {
    UUID interestA = UUID.randomUUID();
    UUID interestB = UUID.randomUUID();

    // interestA 6건, interestB 4건 → 총 10건이라 size 기준 플러시
    for (int i = 0; i < 6; i++) {
      aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestA)));
    }
    for (int i = 0; i < 4; i++) {
      aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestB)));
    }

    Interest a = Interest.builder().name("A").keywords(List.of("a")).build();
    Interest b = Interest.builder().name("B").keywords(List.of("b")).build();
    ReflectionTestUtils.setField(a, "id", interestA);
    ReflectionTestUtils.setField(b, "id", interestB);
    when(interestRepository.findAllById(Set.of(interestA, interestB))).thenReturn(List.of(a, b));

    UUID aUser1 = UUID.randomUUID();
    UUID aUser2 = UUID.randomUUID();
    UUID bUser1 = UUID.randomUUID();
    when(subscribeRepository.findByInterestIdIn(List.of(interestA, interestB)))
        .thenReturn(List.of(
            Subscribe.builder().userId(aUser1).interestId(interestA).build(),
            Subscribe.builder().userId(aUser2).interestId(interestA).build(),
            Subscribe.builder().userId(bUser1).interestId(interestB).build()
        ));

    ArgumentCaptor<NotificationCreateDto> captor = ArgumentCaptor.forClass(NotificationCreateDto.class);

    // when
    aggregator.checkFlush();

    // then: A 구독자 2명, B 구독자 1명 → 총 3건 발행
    verify(publisher, times(3)).publishEvent(captor.capture());
    List<NotificationCreateDto> events = captor.getAllValues();
    assertThat(events).filteredOn(dto -> dto.resourceId().equals(interestA)).hasSize(2)
        .allMatch(dto -> dto.context().contains("6건"));
    assertThat(events).filteredOn(dto -> dto.resourceId().equals(interestB)).hasSize(1)
        .allMatch(dto -> dto.context().contains("4건"));
  }

  @Test
  @DisplayName("시간/사이즈 조건 모두 미충족 시 플러시하지 않는다")
  void noFlushWhenBelowThreshold() {
    UUID interestId = UUID.randomUUID();
    aggregator.onArticlesCollected(new ArticlesCollectedEvent(UUID.randomUUID(), Set.of(interestId)));

    // lastFlush는 초기값 그대로(최근) → 시간 조건 미충족, 건수도 1건
    aggregator.checkFlush();

    verify(publisher, never()).publishEvent(any());
  }
}
