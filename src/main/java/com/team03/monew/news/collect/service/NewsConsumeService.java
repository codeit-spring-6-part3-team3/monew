package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.news.collect.domain.FilteredNewsTask;
import com.team03.monew.news.collect.event.NewsCollectedEvent;
import com.team03.monew.news.collect.infrastructure.queue.NewsQueue;
import com.team03.monew.news.collect.mapper.NewsMapper;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.service.NewsService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsConsumeService {

  private final NewsQueue newsQueue;
  private final NewsService newsService;
  private final NewsMapper newsMapper;
  private final ApplicationEventPublisher eventPublisher;

  private Thread consumerThread;
  private volatile boolean running = true;

  @PostConstruct
  public void startConsumerThread() {
    consumerThread = new Thread(this::consumeLoop, "news-consumer-thread");
    consumerThread.setDaemon(true);
    consumerThread.start();
    log.info("News consumer thread started.");
  }

  @PreDestroy
  public void stopConsumerThread() {
    running = false;
    if (consumerThread != null) {
      consumerThread.interrupt();
    }
  }

  private void consumeLoop() {
    while (running) {
      try {
        FilteredNewsTask task = newsQueue.take();

        // 1) 뉴스 저장
        NewsCreateRequest req = newsMapper.toCreateRequest(task.news());
        var savedNews = newsService.createNews(req);

        log.debug("뉴스 저장 완료: {}", savedNews);
        // 2) 매칭된 관심사에서 id만 추출
        Set<UUID> interestIds = task.matchedInterests().stream()
            .map(Interest::getId)
            .collect(Collectors.toSet());

        var event = new NewsCollectedEvent(
            savedNews.id(),
            interestIds
        );

        log.debug("뉴스 수집 이벤트 발행: {}", event);

        eventPublisher.publishEvent(
           event
        );

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        running = false;
        return;
      } catch (Exception e) {
        log.error("뉴스 처리 중 오류 발생: {}", e.getMessage(), e);
        sleepQuietly(200); // 짧은 백오프로 과도한 루프 방지
      }
    }
  }

  private void sleepQuietly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      running = false;
    }
  }
}
