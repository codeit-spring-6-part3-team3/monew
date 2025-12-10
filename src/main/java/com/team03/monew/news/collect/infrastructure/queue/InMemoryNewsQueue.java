package com.team03.monew.news.collect.infrastructure.queue;

import com.team03.monew.news.collect.domain.FilteredNewsTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;

@Component
public class InMemoryNewsQueue implements NewsQueue {

  private final BlockingQueue<FilteredNewsTask> queue = new LinkedBlockingQueue<>();

  @Override
  public void publish(FilteredNewsTask news) {
    queue.offer(news);
  }

  @Override
  public FilteredNewsTask take() throws InterruptedException {
    return queue.take();
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }
}