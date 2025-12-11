package com.team03.monew.articleCollect.domain;

import com.team03.monew.article.domain.ArticleSourceType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FetchedArticles(
    ArticleSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overview
) {

  @Override
  public String toString() {
    return "FetchedArticles{" +
        "title='" + title + '\'' +
        ", link='" + resourceLink + '\'' +
        ", press='" + source.toString() + '\'' +
        ", pubDate=" + postDate.toString() +
        ", overview='" + overview + '\'' +
        '}';
  }
}