package com.team03.monew.articleView.service;

import com.team03.monew.article.domain.Article;
import java.util.UUID;

public interface ArticleViewsService {
  boolean isRead(Article article, UUID userId);
}
