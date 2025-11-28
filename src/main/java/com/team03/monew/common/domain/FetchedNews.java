package com.team03.monew.common.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class FetchedNews {
  private final Press source;
  private final String resourceLink;
  private final String title;
  private final LocalDateTime postDate;
  private final String overview;

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