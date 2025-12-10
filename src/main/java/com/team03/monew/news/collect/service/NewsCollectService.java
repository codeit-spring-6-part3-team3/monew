package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.FilteredNewsTask;
import com.team03.monew.news.collect.infrastructure.client.RssClient;
import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.collect.infrastructure.queue.NewsQueue;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCollectService implements CollectService {

  private final RssClient rssClient;
  private final NewsQueue newsQueue;
  private final KeywordFilterService keywordFilterService;

  public void fetchAllNews() {
    List<NewsFeed> feeds = Arrays.stream(NewsFeed.values()).toList();

    feeds.forEach(feed -> {
      try {
        rssClient.fetchAndParse(feed, this::handleOneNews);
      } catch (Exception e) {
        log.error("Failed to fetch feed: {}", feed.getUrl(), e);
      }
    });
  }

  void handleOneNews(FetchedNews news) {
    Set<Interest> matched = keywordFilterService.matchingInterests(news);

    if (!matched.isEmpty()) {
      newsQueue.publish(new FilteredNewsTask(news, matched));
    }
  }
}