package com.team03.monew.news.dto;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record NewsDto(
    UUID id,
    NewsSourceType source,
    String sourceLink,
    String title,
    LocalDateTime publishDate,
    String summary,
    Long commentCount,
    Long viewCount,
    boolean viewedByMe
) {
  public static NewsDto from(News news, boolean viewedByMe) {
    return new NewsDto(
        news.getArticleId(),
        news.getSource(),
        news.getResourceLink(),
        news.getTitle(),
        news.getPostDate(),
        news.getOverview(),
        news.getCommentCount(),
        news.getViewCount(),
        viewedByMe
    );
  }
}
