package com.team03.monew.articleCollect.infrastructure.client;

import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.articleCollect.domain.ArticlesFeed;
import java.util.function.Consumer;

public interface RssClient {

  void fetchAndParse(ArticlesFeed feed, Consumer<FetchedArticles> sink);
}