package com.team03.monew.news.collect.infrastructure.client;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.NewsFeed;
import java.util.function.Consumer;

public interface RssClient {

  void fetchAndParse(NewsFeed feed, Consumer<FetchedNews> sink);
}