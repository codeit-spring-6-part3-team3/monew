package com.team03.monew.common.service;

import com.team03.monew.common.domain.FetchedNews;
import com.team03.monew.common.infrastructure.client.RssClient;
import com.team03.monew.common.domain.NewsFeed;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCollectService implements CollectService {

  private final RssClient rssClient;
//  private final NewsSaver newsSaver;

  public void fetchAllNews() {
    List<NewsFeed> feeds = Arrays.stream(NewsFeed.values()).toList();

    feeds.forEach(feed -> {
      try {
        List<FetchedNews> fetched = rssClient.fetch(feed);
//        newsSaver.saveAll(fetched);
      } catch (Exception e) {
        log.error("Failed to fetch feed: {}", feed.getUrl(), e);
      }
    });
  }
}