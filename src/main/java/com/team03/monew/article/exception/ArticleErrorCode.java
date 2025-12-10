package com.team03.monew.article.exception;

import com.team03.monew.common.customerror.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ArticleErrorCode implements ErrorCode {

  ARTICLE_DUPLICATE_LINK(HttpStatus.CONFLICT, "이미 존재하는 뉴스 기사"),

  // 뉴스 조회, 삭제
  ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "뉴스 기사 없음"),

  // 삭제 불가
  ARTICLE_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "삭제할 수 없는 상태");


  private final HttpStatus httpStatus;
  private final String message;

  ArticleErrorCode(HttpStatus httpStatus, String message) {
    this.httpStatus = httpStatus;
    this.message = message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
