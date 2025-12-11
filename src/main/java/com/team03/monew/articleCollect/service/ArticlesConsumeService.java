package com.team03.monew.articleCollect.service;

import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.service.ArticleService;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.articleCollect.domain.FilteredArticlesTask;
import com.team03.monew.articleCollect.event.ArticlesCollectedEvent;
import com.team03.monew.articleCollect.infrastructure.queue.ArticlesQueue;
import com.team03.monew.articleCollect.mapper.ArticlesMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Objects;
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
public class ArticlesConsumeService {

  private final ArticlesQueue articlesQueue;
  private final ArticleService articlesService;
  private final ArticlesMapper articlesMapper;
  private final ApplicationEventPublisher eventPublisher;

  private Thread consumerThread;
  private volatile boolean running = true;

  @PostConstruct
  public void startConsumerThread() {
    consumerThread = new Thread(this::consumeLoop, "articles-consumer-thread");
    consumerThread.setDaemon(true);
    consumerThread.start();
    log.info("Articles consumer thread started.");
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
        FilteredArticlesTask task = articlesQueue.take();

        // 1) 기사 저장 (매칭된 관심사 중 하나로 저장 시도)
        ArticleCreateRequest req = articlesMapper.toCreateRequest(task.article());
        var savedArticle = task.matchedInterests().stream()
            .map(Interest::getId)
            .map(interestId -> {
              try {
                return articlesService.createArticle(req, interestId);
              } catch (Exception e) {
                log.debug("기사 저장 스킵 (interestId={}): {}", interestId, e.getMessage());
                return null;
              }
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);

        if (savedArticle == null) {
          log.debug("저장된 기사 없음 → 다음 작업으로 넘어갑니다");
          continue;
        }

        log.debug("기사 저장 완료: {}", savedArticle);
        // 2) 매칭된 관심사에서 id만 추출
        Set<UUID> interestIds = task.matchedInterests().stream()
            .map(Interest::getId)
            .collect(Collectors.toSet());

        var event = new ArticlesCollectedEvent(
            savedArticle.id(),
            interestIds
        );

        log.debug("기사 수집 이벤트 발행: {}", event);

        eventPublisher.publishEvent(
           event
        );

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        running = false;
        return;
      } catch (Exception e) {
        log.error("기사 처리 중 오류 발생: {}", e.getMessage(), e);
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
