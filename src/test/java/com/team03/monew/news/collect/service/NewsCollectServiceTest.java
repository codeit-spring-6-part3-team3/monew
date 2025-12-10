package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.FilteredNewsTask;
import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.collect.infrastructure.client.RssClient;
import com.team03.monew.news.collect.infrastructure.queue.NewsQueue;
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
class NewsCollectServiceTest {

  @Mock
  RssClient rssClient;
  @Mock
  NewsQueue newsQueue;
  @Mock
  KeywordFilterService keywordFilterService;

  @InjectMocks
  NewsCollectService collectService;

  @Test
  @DisplayName("fetchAllNews는 모든 피드에 대해 fetchAndParse를 호출하고 예외가 발생해도 나머지를 계속 처리한다")
  void fetchAllNews_callsAllFeeds_andContinuesOnError() {
    // 하나의 피드에서 예외 발생
    doThrow(new RuntimeException("fetch fail"))
        .when(rssClient).fetchAndParse(eq(NewsFeed.CHOSUN_ALL), any());
    // 다른 피드는 정상 no-op
    doNothing().when(rssClient).fetchAndParse(eq(NewsFeed.YONHAP_LATEST), any());
    doNothing().when(rssClient).fetchAndParse(eq(NewsFeed.HANKYUNG_ALL_NEWS), any());

    collectService.fetchAllNews();

    // 모든 피드에 대해 시도했는지 검증
    verify(rssClient).fetchAndParse(eq(NewsFeed.CHOSUN_ALL), any());
    verify(rssClient).fetchAndParse(eq(NewsFeed.YONHAP_LATEST), any());
    verify(rssClient).fetchAndParse(eq(NewsFeed.HANKYUNG_ALL_NEWS), any());
  }

  @Test
  @DisplayName("handleOneNews: 매칭 관심사가 없으면 큐에 넣지 않는다")
  void handleOneNews_noMatchedInterests() {
    FetchedNews news = new FetchedNews(
        NewsFeed.CHOSUN_ALL.getSource(),
        "url",
        "title",
        LocalDateTime.now(),
        "summary"
    );
    given(keywordFilterService.matchingInterests(news)).willReturn(Set.of());

    collectService.handleOneNews(news);

    verify(newsQueue, never()).publish(any());
  }

  @Test
  @DisplayName("handleOneNews: 매칭 관심사가 있으면 FilteredNewsTask로 큐에 publish한다")
  void handleOneNews_matchedInterests_publish() {
    FetchedNews news = new FetchedNews(
        NewsFeed.CHOSUN_ALL.getSource(),
        "url",
        "title",
        LocalDateTime.now(),
        "summary"
    );
    Interest interest = Interest.builder().build();
    Set<Interest> matched = Set.of(interest);
    given(keywordFilterService.matchingInterests(news)).willReturn(matched);

    ArgumentCaptor<FilteredNewsTask> taskCaptor = ArgumentCaptor.forClass(FilteredNewsTask.class);

    collectService.handleOneNews(news);

    verify(newsQueue, times(1)).publish(taskCaptor.capture());
    FilteredNewsTask task = taskCaptor.getValue();
    assertThat(task.news()).isEqualTo(news);
    assertThat(task.matchedInterests()).isEqualTo(matched);
  }
}
