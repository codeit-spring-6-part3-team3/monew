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
            ArticleViews articleViews
    ) {
        this(
                articleViews.getId(),
                articleViews.getUser().getId(),
                articleViews.getCreatedAt(),
                articleViews.getArticle().getId(),
                articleViews.getArticle().getSource(),
                articleViews.getArticle().getResourceLink(),
                articleViews.getArticle().getTitle(),
                articleViews.getArticle().getPostedAt(),
                articleViews.getArticle().getOverview(),
                articleViews.getArticle().getCommentCount(),
                articleViews.getArticle().getViewCount()
        );
    }
}
