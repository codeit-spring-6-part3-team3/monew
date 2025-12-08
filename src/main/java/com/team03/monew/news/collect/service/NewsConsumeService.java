package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.news.collect.domain.FilteredNewsTask;
import com.team03.monew.news.collect.event.NewsCollectedEvent;
import com.team03.monew.news.collect.infrastructure.queue.NewsQueue;
import com.team03.monew.news.collect.mapper.NewsMapper;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.service.NewsService;
import jakarta.annotation.PostConstruct;
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

  @PostConstruct
  public void startConsumerThread() {
    Thread consumer = new Thread(this::consumeLoop, "news-consumer-thread");
    consumer.setDaemon(true); // 앱 종료 시 자동 종료되도록
    consumer.start();
    log.info("News consumer thread started.");
  }

  private void consumeLoop() {
    while (true) {
      try {
        FilteredNewsTask task = newsQueue.take();

        // 1) 뉴스 저장
        NewsCreateRequest req = newsMapper.toCreateRequest(task.news());
        var savedNews = newsService.createNews(req); // NewsResponseDto 라고 가정 (id, title 포함)

        // 2) 매칭된 관심사에서 id만 추출
        Set<UUID> interestIds = task.matchedInterests().stream()
            .map(Interest::getId)
            .collect(Collectors.toSet());

        // 3) 이벤트 발행
        eventPublisher.publishEvent(
            new NewsCollectedEvent(
                savedNews.id(),
                interestIds
            )
        );

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      } catch (Exception e) {
        log.error("뉴스 처리 중 오류 발생", e);
      }
    }
  }
}