package com.team03.monew.articleCollect.scheduler;

import com.team03.monew.articleCollect.service.CollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlesRssScheduler {

  private final CollectService articlesCollectService;

  @Scheduled(cron = "0 1 * * * *")
  public void collectArticles() {
    articlesCollectService.fetchAllArticles();
  }
}
