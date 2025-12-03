package com.team03.monew.news.collect.domain;

import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FetchedNews(
    NewsSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overview
) {

  @Override
  public String toString() {
    return "FetchedNews{" +
        "title='" + title + '\'' +
        ", link='" + resourceLink + '\'' +
        ", press='" + source.toString() + '\'' +
        ", pubDate=" + postDate.toString() +
        ", overview='" + overview + '\'' +
        '}';
  }
}