package com.team03.monew.news.collect.event;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.subscribe.repository.SubscribeRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
class NewsNotificationBatchAggregatorTest {

  @Mock
  InterestRepository interestRepository;
  @Mock
  SubscribeRepository subscribeRepository;
  @Mock
  ApplicationEventPublisher publisher;

  @InjectMocks
  NewsNotificationBatchAggregator aggregator;

  @Test
  @DisplayName("버퍼 크기가 임계값 이상이면 즉시 flush하여 알림 이벤트를 발행한다")
  void flushBySize() {
    // given: 동일 관심사에 대한 10건을 적재
    UUID interestId = UUID.randomUUID();
    for (int i = 0; i < 10; i++) {
      aggregator.onNewsCollected(new NewsCollectedEvent(UUID.randomUUID(), Set.of(interestId)));
    }

    Interest interest = Interest.builder().name("테스트").keywords(List.of("k")).build();
    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of(interest));
    when(subscribeRepository.findUserIdsByInterestId(interestId))
        .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));

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
    aggregator.onNewsCollected(new NewsCollectedEvent(UUID.randomUUID(), Set.of(interestId)));

    // lastFlush를 과거로 설정해 시간 조건 만족시킴
    ReflectionTestUtils.setField(aggregator, "lastFlush", Instant.now().minusMillis(4_000));

    Interest interest = Interest.builder().name("시사").keywords(List.of("a")).build();
    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of(interest));
    when(subscribeRepository.findUserIdsByInterestId(interestId))
        .thenReturn(List.of(UUID.randomUUID()));

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
    aggregator.onNewsCollected(new NewsCollectedEvent(UUID.randomUUID(), Set.of(interestId)));

    // 시간 조건을 만족시켜 flush 실행
    ReflectionTestUtils.setField(aggregator, "lastFlush", Instant.now().minusMillis(4_000));

    when(interestRepository.findAllById(Set.of(interestId))).thenReturn(List.of());
    when(subscribeRepository.findUserIdsByInterestId(interestId)).thenReturn(List.of());

    aggregator.checkFlush();

    verify(publisher, never()).publishEvent(any());
  }
}
