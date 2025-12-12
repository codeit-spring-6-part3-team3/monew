package com.team03.monew.article.dto;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.domain.ArticleSourceType;
import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleResponseDto(
    UUID id,
    ArticleSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overView
) {
  public static ArticleResponseDto from(Article article) {
    return new ArticleResponseDto(
        article.getId(),
        article.getSource(),
        article.getResourceLink(),
        article.getTitle(),
        article.getPostedAt(),
        article.getOverview()
    );
  }
}
