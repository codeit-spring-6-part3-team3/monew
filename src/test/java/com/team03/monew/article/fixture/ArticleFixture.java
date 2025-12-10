package com.team03.monew.article.fixture;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.dto.ArticleCreateRequest;

public class ArticleFixture {

  public static Article articleCreate(ArticleCreateRequest articleCreateRequest) {
    return Article.builder()
        .source(articleCreateRequest.source())
        .resourceLink(articleCreateRequest.resourceLink())
        .title(articleCreateRequest.title())
        .postedAt(articleCreateRequest.postDate())
        .overview(articleCreateRequest.overView())
        .build();
  }
}