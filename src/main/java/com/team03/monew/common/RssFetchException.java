package com.team03.monew.common;

import com.team03.monew.common.domain.NewsFeed;
import lombok.Getter;

@Getter
public class RssFetchException extends RuntimeException {

  private final NewsFeed feed;

  public RssFetchException(NewsFeed feed, String message, Throwable cause) {
    super(message, cause);
    this.feed = feed;
  }
}