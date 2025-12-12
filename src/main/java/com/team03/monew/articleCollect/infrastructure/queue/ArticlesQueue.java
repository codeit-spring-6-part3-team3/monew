package com.team03.monew.articleCollect.infrastructure.queue;

import com.team03.monew.articleCollect.domain.FilteredArticlesTask;

public interface ArticlesQueue {

  void publish(FilteredArticlesTask article);

  FilteredArticlesTask take() throws InterruptedException;

  boolean isEmpty();
}
