package com.team03.monew.news.collect.scheduler;

import com.team03.monew.news.collect.service.NewsCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsRssScheduler {

  private final NewsCollectService newsCollectService;

  @Scheduled(cron = "0 0 * * * *")
  public void collectNews() {
    newsCollectService.fetchAllNews();
  }
}