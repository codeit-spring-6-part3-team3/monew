package com.team03.monew.news.collect.infrastructure.client;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.NewsFeed;
import java.util.List;

public interface RssClient {

  List<FetchedNews> fetch(NewsFeed feed);
}