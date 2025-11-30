package com.team03.monew.news.collect.exception;

import com.team03.monew.news.collect.domain.NewsFeed;
import lombok.Getter;

@Getter
public class RssFetchException extends RuntimeException {

  private final NewsFeed feed;

  public RssFetchException(NewsFeed feed, String message, Throwable cause) {
    super(message, cause);
    this.feed = feed;
  }
}