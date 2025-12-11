package com.team03.monew.articleviews.service;

import com.team03.monew.article.domain.Article;
import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;

import java.util.List;
import java.util.UUID;

public interface ArticleViewsService {
  boolean isRead(Article article, UUID userId);
  List<ArticleViewsActivityDto> topTenByUserId(UUID userId);
}
