package com.team03.monew.news.exception;

public class CustomException {

  // 중복되는 링크 들어갈시 발생하는 에외
  public static class SameResourceLink extends RuntimeException {
    public SameResourceLink() {
      super("이미 존재하는 뉴스 기사입니다.");
    }

  }

}