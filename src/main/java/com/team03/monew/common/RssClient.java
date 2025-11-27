package com.team03.monew.common;

import com.team03.monew.common.domain.NewsFeed;
import java.util.List;

public interface RssClient {

  List<FetchedNews> fetch(NewsFeed feed);
}