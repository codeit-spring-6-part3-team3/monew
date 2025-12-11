package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import java.util.function.Consumer;

public interface RssParser {
  boolean supports(ArticleSourceType source);
  void parse(String xml, Consumer<FetchedArticles> sink);
}