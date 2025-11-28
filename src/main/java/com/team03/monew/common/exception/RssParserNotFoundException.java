package com.team03.monew.common.exception;

import com.team03.monew.common.domain.NewsFeed;
import lombok.Getter;

@Getter
public class RssParserNotFoundException extends RuntimeException {

  private final NewsFeed feed;

  public RssParserNotFoundException(NewsFeed feed) {
    super("No parser for press=" + feed.getPress() + ", feed=" + feed.name());
    this.feed = feed;
  }
}