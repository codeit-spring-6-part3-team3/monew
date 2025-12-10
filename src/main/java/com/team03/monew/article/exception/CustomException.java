package com.team03.monew.article.exception;

public class CustomException {

  // 1. 뉴스 생성 부분
  // 중복된 뉴스 링크
  public static class SameResourceLink extends RuntimeException {
    public SameResourceLink() {
      super("이미 존재하는 뉴스 기사입니다.");
    }

  }

  // 2. 뉴스 삭제 부분
  // 뉴스가 존재하지 않을때
  public static class ArticleNotFound extends RuntimeException {
    public ArticleNotFound() {
      super("존재 하지않는 뉴스 기사입니다.");
    }
  }
  // 뉴스 물리 삭제 시 isDelete가 false일때
  public static class ArticleCanNotDelete extends IllegalStateException {
    public ArticleCanNotDelete() {
      super("현재 뉴스가 논리적으로 삭제되어 있지 않습니다.");
    }
  }
}