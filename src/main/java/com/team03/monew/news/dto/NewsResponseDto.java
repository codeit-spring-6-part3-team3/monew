package com.team03.monew.news.dto;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponseDto (
    UUID id,
    NewsSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overView
) {
  public static NewsResponseDto from(News news) {
    return new NewsResponseDto(
        news.getArticleId(),
        news.getSource(),
        news.getResourceLink(),
        news.getTitle(),
        news.getPostDate(),
        news.getOverview()
    );
  }
}
