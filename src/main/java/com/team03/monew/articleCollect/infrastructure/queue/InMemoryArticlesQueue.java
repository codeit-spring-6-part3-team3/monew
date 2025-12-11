package com.team03.monew.articleCollect.infrastructure.queue;

import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryArticlesQueue implements ArticlesQueue {

  private final BlockingQueue<FilteredArticlesTask> queue = new LinkedBlockingQueue<>();

  @Override
  public void publish(FilteredArticlesTask article) {
    queue.offer(article);
  }

  @Override
  public FilteredArticlesTask take() throws InterruptedException {
    return queue.take();
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }
}
