package com.team03.monew.news.collect.infrastructure.queue;

import com.team03.monew.news.collect.domain.FilteredNewsTask;

public interface NewsQueue {

  void publish(FilteredNewsTask news);

  FilteredNewsTask take() throws InterruptedException;

  boolean isEmpty();
}