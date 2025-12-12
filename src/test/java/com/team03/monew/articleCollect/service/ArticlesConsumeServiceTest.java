package com.team03.monew.articleCollect.service;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleResponseDto;
import com.team03.monew.article.service.ArticleService;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import com.team03.monew.articleCollect.event.ArticlesCollectedEvent;
import com.team03.monew.articleCollect.infrastructure.queue.ArticlesQueue;
import com.team03.monew.articleCollect.mapper.ArticlesMapper;
import com.team03.monew.interest.domain.Interest;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticlesConsumeServiceTest {

  @Mock
  ArticlesQueue articlesQueue;
  @Mock
  ArticleService articleService;
  @Mock
  ArticlesMapper articlesMapper;
  @Mock
  ApplicationEventPublisher eventPublisher;

  @InjectMocks
  ArticlesConsumeService consumeService;

  @Test
  @DisplayName("consumeLoop: 기사 저장 후 관심사 ID 집합으로 이벤트를 발행한다")
  void consumeLoop_publishesEvent() throws Exception {
    UUID interestId = UUID.randomUUID();
    Interest interest = Interest.builder()
        .name("tech")
        .keywords(List.of("AI"))
        .build();
    ReflectionTestUtils.setField(interest, "id", interestId);

    var fetched = new FetchedArticles(
        ArticleSourceType.CHOSUN,
        "https://example.com",
        "title",
        LocalDateTime.now(),
        "overview"
    );

    FilteredArticlesTask task = new FilteredArticlesTask(fetched, Set.of(interest));

    ArticleCreateRequest req = new ArticleCreateRequest(
        fetched.source(),
        fetched.resourceLink(),
        fetched.title(),
        fetched.postDate(),
        fetched.overview()
    );
    UUID savedId = UUID.randomUUID();
    ArticleResponseDto saved = new ArticleResponseDto(
        savedId, fetched.source(), fetched.resourceLink(), fetched.title(), fetched.postDate(), fetched.overview());

    given(articlesMapper.toCreateRequest(fetched)).willReturn(req);
    given(articleService.createArticle(req, interestId)).willReturn(saved);

    when(articlesQueue.take())
        .thenReturn(task)
        .thenThrow(new InterruptedException("stop"));

    Thread t = new Thread(() -> ReflectionTestUtils.invokeMethod(consumeService, "consumeLoop"));
    t.start();
    t.join(1000);
    consumeService.stopConsumerThread();

    verify(articlesMapper, times(1)).toCreateRequest(fetched);
    verify(articleService, times(1)).createArticle(req, interestId);

    ArgumentCaptor<ArticlesCollectedEvent> eventCaptor = ArgumentCaptor.forClass(
        ArticlesCollectedEvent.class);
    verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

    ArticlesCollectedEvent event = eventCaptor.getValue();
    assertThat(event.articleId()).isEqualTo(savedId);
    assertThat(event.interestIds()).containsExactlyInAnyOrder(interestId);
  }
}
