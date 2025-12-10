package com.team03.monew.article.exception;

import static com.team03.monew.article.exception.ArticleErrorCode.ARTICLE_CANNOT_DELETE;

import com.team03.monew.common.customerror.MonewException;

public class ArticleCanTDeleteException extends MonewException {

  public ArticleCanTDeleteException() {
    super(ARTICLE_CANNOT_DELETE);
  }
}
