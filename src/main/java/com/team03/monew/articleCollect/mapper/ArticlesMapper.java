package com.team03.monew.articleCollect.mapper;

import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.articleCollect.domain.FetchedArticles;

import org.springframework.stereotype.Component;

@Component
public class ArticlesMapper {
  public ArticleCreateRequest toCreateRequest(FetchedArticles fetched) {
    return new ArticleCreateRequest(
        fetched.source(),      // enum
        fetched.resourceLink(),        // resourceLink
        fetched.title(),       // title
        fetched.postDate(),    // postDate (null이면 서비스에서 처리하게 둘 수도)
        fetched.overview()     // overView
    );
  }
}
