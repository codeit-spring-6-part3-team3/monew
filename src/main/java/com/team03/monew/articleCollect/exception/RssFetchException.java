package com.team03.monew.articleCollect.exception;

import com.team03.monew.articleCollect.domain.ArticlesFeed;
import lombok.Getter;

@Getter
public class RssFetchException extends RuntimeException {

  private final ArticlesFeed feed;

  public RssFetchException(ArticlesFeed feed, String message, Throwable cause) {
    super(message, cause);
    this.feed = feed;
  }
}