package com.team03.monew.articleCollect.exception;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleCollect.domain.ArticlesFeed;
import lombok.Getter;

@Getter
public class RssParserNotFoundException extends RuntimeException {

  private final ArticleSourceType source;
  private final ArticlesFeed feed;

  // 부팅 시 특정 언론사에 대한 파서를 찾지 못했을 때 사용
  public RssParserNotFoundException(ArticleSourceType source) {
    super("No RssParser supports press: " + source);
    this.source = source;
    this.feed = null;
  }

  // 런타임에 요청한 feed에 대한 파서를 찾지 못했을 때 사용
  public RssParserNotFoundException(ArticlesFeed feed) {
    super("No parser for press=" + feed.getSource() + ", feed=" + feed.name());
    this.feed = feed;
    this.source = feed.getSource();
  }
}
