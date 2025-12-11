package com.team03.monew.articleviews.dto;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleviews.domain.ArticleViews;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleViewsActivityDto(
        UUID id,
        UUID viewedBy,
        LocalDateTime createdAt,
        UUID articleId,
        ArticleSourceType source,
        String sourceUrl,
        String articleTitle,
        LocalDateTime articlePublishedAt,
        String articleSummary,
        Long articleCommentCount,
        Long articleViewCount
) {
    public ArticleViewsActivityDto(
            UUID viewedBy,
            LocalDateTime createdAt,
            Article article,
            ArticleViews articleViews
    ) {
        this(
                articleViews.getId(),
                viewedBy,
                createdAt,
                article.getId(),
                article.getSource(),
                article.getResourceLink(),
                article.getTitle(),
                article.getPostedAt(),
                article.getOverview(),
                article.getCommentCount(),
                article.getViewCount()
        );
    }
}
