package com.team03.monew.news.collect.scheduler;

import com.team03.monew.news.collect.service.NewsCollectService;
import org.springframework.scheduling.annotation.Scheduled;

public class NewsRssScheduler {

  NewsCollectService newsCollectService;

  @Scheduled(cron = "0 0 * * * *")
  public void collectNews() {
    newsCollectService.fetchAllNews();
  }
}