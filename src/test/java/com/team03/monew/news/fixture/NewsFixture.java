package com.team03.monew.news.fixture;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.dto.NewsCreateRequest;

public class NewsFixture {

  public static News newsCreate(NewsCreateRequest newsCreateRequest) {
    return News.builder()
        .source(newsCreateRequest.source())
        .resourceLink(newsCreateRequest.resourceLink())
        .title(newsCreateRequest.title())
        .postDate(newsCreateRequest.postDate())
        .overview(newsCreateRequest.overView())
        .build();
  }

}