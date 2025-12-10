package com.team03.monew.article.exception;

import static com.team03.monew.article.exception.ArticleErrorCode.ARTICLE_DUPLICATE_LINK;

import com.team03.monew.common.customerror.MonewException;

public class DuplicateResourceLinkException extends MonewException {

  public DuplicateResourceLinkException() {
    super(ARTICLE_DUPLICATE_LINK);
  }
}
