package com.team03.monew.news.collect.exception;

import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.domain.NewsSourceType;
import lombok.Getter;

@Getter
public class RssParserNotFoundException extends RuntimeException {

  private final NewsSourceType source;
  private final NewsFeed feed;

  // 부팅 시 특정 언론사에 대한 파서를 찾지 못했을 때 사용
  public RssParserNotFoundException(NewsSourceType source) {
    super("No RssParser supports press: " + source);
    this.source = source;
    this.feed = null;
  }

  // 런타임에 요청한 feed에 대한 파서를 찾지 못했을 때 사용
  public RssParserNotFoundException(NewsFeed feed) {
    super("No parser for press=" + feed.getSource() + ", feed=" + feed.name());
    this.feed = feed;
    this.source = feed.getSource();
  }
}
