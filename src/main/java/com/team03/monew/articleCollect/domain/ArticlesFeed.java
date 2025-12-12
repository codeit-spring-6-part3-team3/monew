package com.team03.monew.articleCollect.domain;

import com.team03.monew.article.domain.ArticleSourceType;
import lombok.Getter;

@Getter
public enum ArticlesFeed {

  // --------------------------
  // 📌 조선일보 (CHOSUN)
  // --------------------------
  CHOSUN_ALL(
      ArticleSourceType.CHOSUN,
      "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml"
  ),

  // --------------------------
  // 📌 연합뉴스 (YONHAP)
  // --------------------------
  YONHAP_LATEST(
      ArticleSourceType.YNA,
      "https://www.yonhapnewstv.co.kr:443/browse/feed"
  ),

  // --------------------------
  // 📌 한국경제 (HANKYUNG)
  // --------------------------
  HANKYUNG_ALL_NEWS(
      ArticleSourceType.KOREA,
      "https://www.hankyung.com/feed/all-news"
  );

  private final ArticleSourceType source;
  private final String url;

  ArticlesFeed(ArticleSourceType source, String url) {
    this.source = source;
    this.url = url;
  }
}