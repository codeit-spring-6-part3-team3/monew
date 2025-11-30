package com.team03.monew.news.collect.domain;

import com.team03.monew.news.domain.NewsSourceType;
import lombok.Getter;

@Getter
public enum NewsFeed {

  // --------------------------
  // 📌 조선일보 (CHOSUN)
  // --------------------------
  CHOSUN_ALL(
      NewsSourceType.chosun,
      "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml"
  ),

  // --------------------------
  // 📌 연합뉴스 (YONHAP)
  // --------------------------
  YONHAP_LATEST(
      NewsSourceType.yna,
      "http://www.yonhapnewstv.co.kr/browse/feed/"
  ),

  // --------------------------
  // 📌 한국경제 (HANKYUNG)
  // --------------------------
  HANKYUNG_ALL_NEWS(
      NewsSourceType.korea,
      "https://www.hankyung.com/feed/all-news"
  );

  private final NewsSourceType source;
  private final String url;

  NewsFeed(NewsSourceType source, String url) {
    this.source = source;
    this.url = url;
  }
}