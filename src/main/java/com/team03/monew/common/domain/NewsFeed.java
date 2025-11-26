package com.team03.monew.common.domain;

import lombok.Getter;

@Getter
public enum NewsFeed {

  // --------------------------
  // 📌 조선일보 (CHOSUN)
  // --------------------------
  CHOSUN_ALL(
      Press.CHOSUN,
      "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml"
  ),

  // --------------------------
  // 📌 연합뉴스 (YONHAP)
  // --------------------------
  YONHAP_LATEST(
      Press.YONHAP,
      "http://www.yonhapnewstv.co.kr/browse/feed/"
  ),

  // --------------------------
  // 📌 한국경제 (HANKYUNG)
  // --------------------------
  HANKYUNG_ALL_NEWS(
      Press.HANKYUNG,
      "https://www.hankyung.com/feed/all-news"
  );

  private final Press press;
  private final String url;

  NewsFeed(Press press, String url) {
    this.press = press;
    this.url = url;
  }
}