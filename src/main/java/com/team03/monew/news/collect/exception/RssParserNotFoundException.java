package com.team03.monew.news.collect.exception;

import com.team03.monew.news.collect.domain.NewsFeed;
import lombok.Getter;

@Getter
public class RssParserNotFoundException extends RuntimeException {

  private final NewsFeed feed;

  public RssParserNotFoundException(NewsFeed feed) {
    super("No parser for press=" + feed.getSource() + ", feed=" + feed.name());
    this.feed = feed;
  }
}