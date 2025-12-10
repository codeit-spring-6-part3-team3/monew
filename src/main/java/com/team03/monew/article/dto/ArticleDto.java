package com.team03.monew.article.dto;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.domain.ArticleSourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleDto(
    UUID id,
    ArticleSourceType source,
    String sourceLink,
    String title,
    LocalDateTime publishDate,
    String summary,
    Long commentCount,
    Long viewCount,
    boolean viewedByMe
) {
  public static ArticleDto from(Article article, boolean viewedByMe) {
    return new ArticleDto(
        article.getId(),
        article.getSource(),
        article.getResourceLink(),
        article.getTitle(),
        article.getPostedAt(),
        article.getOverview(),
        article.getCommentCount(),
        article.getViewCount(),
        viewedByMe
    );
  }
}
