package com.team03.monew.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class NewsRssScheduler {

  @Scheduled(cron = "0 0 * * * *")
  public void collectNews() {

  }
}
