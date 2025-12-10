package com.team03.monew.article.exception;

import static com.team03.monew.article.exception.ArticleErrorCode.ARTICLE_NOT_FOUND;

import com.team03.monew.common.customerror.MonewException;

public class ArticleNotFoundException extends MonewException {

  public ArticleNotFoundException() {
    super(ARTICLE_NOT_FOUND);
  }
}
