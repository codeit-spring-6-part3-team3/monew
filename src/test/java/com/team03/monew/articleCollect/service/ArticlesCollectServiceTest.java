package com.team03.monew.articleCollect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import com.team03.monew.articleCollect.domain.ArticlesFeed;
import com.team03.monew.articleCollect.infrastructure.client.RssClient;
import com.team03.monew.articleCollect.infrastructure.queue.ArticlesQueue;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticlesCollectServiceTest {

  @Mock
  RssClient rssClient;
  @Mock
  ArticlesQueue articlesQueue;
  @Mock
  KeywordFilterService keywordFilterService;

  @InjectMocks
  ArticlesCollectService collectService;

  @Test
  @DisplayName("fetchAllArticles는 모든 피드에 대해 fetchAndParse를 호출하고 예외가 발생해도 나머지를 계속 처리한다")
  void fetchAllArticles_callsAllFeeds_andContinuesOnError() {
    // 하나의 피드에서 예외 발생
    doThrow(new RuntimeException("fetch fail"))
        .when(rssClient).fetchAndParse(eq(ArticlesFeed.CHOSUN_ALL), any());
    // 다른 피드는 정상 no-op
    doNothing().when(rssClient).fetchAndParse(eq(ArticlesFeed.YONHAP_LATEST), any());
    doNothing().when(rssClient).fetchAndParse(eq(ArticlesFeed.HANKYUNG_ALL_NEWS), any());

    collectService.fetchAllArticles();

    // 모든 피드에 대해 시도했는지 검증
    verify(rssClient).fetchAndParse(eq(ArticlesFeed.CHOSUN_ALL), any());
    verify(rssClient).fetchAndParse(eq(ArticlesFeed.YONHAP_LATEST), any());
    verify(rssClient).fetchAndParse(eq(ArticlesFeed.HANKYUNG_ALL_NEWS), any());
  }

  @Test
  @DisplayName("handleOneArticle: 매칭 관심사가 없으면 큐에 넣지 않는다")
  void handleOneArticle_noMatchedInterests() {
    FetchedArticles article = new FetchedArticles(
        ArticlesFeed.CHOSUN_ALL.getSource(),
        "url",
        "title",
        LocalDateTime.now(),
        "summary"
    );
    given(keywordFilterService.matchingInterests(article)).willReturn(Set.of());

    collectService.handleOneArticle(article);

    verify(articlesQueue, never()).publish(any());
  }

  @Test
  @DisplayName("handleOneArticle: 매칭 관심사가 있으면 FilteredArticlesTask로 큐에 publish한다")
  void handleOneArticle_matchedInterests_publish() {
    FetchedArticles article = new FetchedArticles(
        ArticlesFeed.CHOSUN_ALL.getSource(),
        "url",
        "title",
        LocalDateTime.now(),
        "summary"
    );
    Interest interest = Interest.builder().build();
    Set<Interest> matched = Set.of(interest);
    given(keywordFilterService.matchingInterests(article)).willReturn(matched);

    ArgumentCaptor<FilteredArticlesTask> taskCaptor = ArgumentCaptor.forClass(FilteredArticlesTask.class);

    collectService.handleOneArticle(article);

    verify(articlesQueue, times(1)).publish(taskCaptor.capture());
    FilteredArticlesTask task = taskCaptor.getValue();
    assertThat(task.article()).isEqualTo(article);
    assertThat(task.matchedInterests()).isEqualTo(matched);
  }
}
