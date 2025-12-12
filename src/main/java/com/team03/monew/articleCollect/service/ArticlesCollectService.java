package com.team03.monew.articleCollect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import com.team03.monew.articleCollect.infrastructure.client.RssClient;
import com.team03.monew.articleCollect.domain.ArticlesFeed;
import com.team03.monew.articleCollect.infrastructure.queue.ArticlesQueue;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticlesCollectService implements CollectService {

  private final RssClient rssClient;
  private final ArticlesQueue articlesQueue;
  private final KeywordFilterService keywordFilterService;

  public void fetchAllArticles() {
    List<ArticlesFeed> feeds = Arrays.stream(ArticlesFeed.values()).toList();

    feeds.forEach(feed -> {
      try {
        rssClient.fetchAndParse(feed, this::handleOneArticle);
      } catch (Exception e) {
        log.error("Failed to fetch feed: {}", feed.getUrl(), e);
      }
    });
  }

  void handleOneArticle(FetchedArticles article) {
    Set<Interest> matched = keywordFilterService.matchingInterests(article);

    if (!matched.isEmpty()) {
      articlesQueue.publish(new FilteredArticlesTask(article, matched));
    }
  }
}
