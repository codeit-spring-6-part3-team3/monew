package com.team03.monew.common.scheduler;

import com.team03.monew.common.service.NewsCollectService;
import org.springframework.scheduling.annotation.Scheduled;

public class NewsRssScheduler {

  NewsCollectService newsCollectService;

  @Scheduled(cron = "0 0 * * * *")
  public void collectNews() {
    newsCollectService.fetchAllNews();
  }
}