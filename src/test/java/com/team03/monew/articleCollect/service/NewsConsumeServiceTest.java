package com.team03.monew.articleCollect.service;

import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import com.team03.monew.articleCollect.event.ArticlesCollectedEvent;
import com.team03.monew.articleCollect.infrastructure.queue.ArticlesQueue;
import com.team03.monew.articleCollect.mapper.ArticlesMapper;
import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.service.NewsService;
import java.time.LocalDateTime;
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
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsConsumeServiceTest {

  @Mock
  ArticlesQueue articlesQueue;
  @Mock
  NewsService newsService;
  @Mock
  ArticlesMapper articlesMapper;
  @Mock
  ApplicationEventPublisher eventPublisher;
  @Mock
  AsyncTaskExecutor asyncTaskExecutor;

  @InjectMocks
  ArticlesConsumeService consumeService;

  @Test
  @DisplayName("consumeLoop: 뉴스 저장 후 관심사 ID 집합으로 이벤트를 발행한다")
  void consumeLoop_publishesEvent() throws Exception {
    // 뉴스/관심사 준비
    UUID newsId = UUID.randomUUID();
    Interest interest = Interest.builder()
        .name("tech")
        .keywords(List.of("AI"))
        .build();
    ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());

    var fetched = new FetchedArticles(
        NewsSourceType.CHOSUN,
        "https://example.com",
        "title",
        LocalDateTime.now(),
        "overview"
    );

    FilteredArticlesTask task = new FilteredArticlesTask(fetched, Set.of(interest));

    // mapper + service stub
    NewsCreateRequest req = new NewsCreateRequest(
        fetched.source(),
        fetched.resourceLink(),
        fetched.title(),
        fetched.postDate(),
        fetched.overview()
    );
    given(articlesMapper.toCreateRequest(fetched)).willReturn(req);
    given(newsService.createNews(req)).willReturn(
        new NewsResponseDto(newsId, fetched.source(), fetched.resourceLink(),
            fetched.title(), fetched.postDate(), fetched.overview())
    );

    // queue takes one task then InterruptedException to stop 루프
    when(articlesQueue.take())
        .thenReturn(task)
        .thenThrow(new InterruptedException("stop"));

    // private consumeLoop 호출을 위한 리플렉션
    Thread t = new Thread(() -> {
      ReflectionTestUtils.invokeMethod(consumeService, "consumeLoop");
    });
    t.start();
    t.join(1000);
    consumeService.stopConsumerThread(); // 테스트 종료 시 플래그/인터럽트

    // 매퍼/서비스 호출 검증
    verify(articlesMapper, times(1)).toCreateRequest(fetched);
    verify(newsService, times(1)).createNews(req);

    // 이벤트 발행 검증
    ArgumentCaptor<ArticlesCollectedEvent> eventCaptor = ArgumentCaptor.forClass(
        ArticlesCollectedEvent.class);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

    ArticlesCollectedEvent event = eventCaptor.getValue();
    assertThat(event.newsId()).isEqualTo(newsId);
    assertThat(event.interestIds()).containsExactlyInAnyOrder(interest.getId());
  }
}
