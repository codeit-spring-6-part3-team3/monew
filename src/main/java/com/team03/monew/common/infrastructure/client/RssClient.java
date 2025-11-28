package com.team03.monew.common.infrastructure.client;

import com.team03.monew.common.domain.FetchedNews;
import com.team03.monew.common.domain.NewsFeed;
import java.util.List;

public interface RssClient {

  List<FetchedNews> fetch(NewsFeed feed);
}