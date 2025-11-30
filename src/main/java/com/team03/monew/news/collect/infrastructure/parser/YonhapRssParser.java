// 연합뉴스TV용 파서
package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;



@Component
public class YonhapRssParser extends BaseDomRssParser {

  private static final String DC_NS = "http://purl.org/dc/elements/1.1/";
  private static final String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";
//  private static final String DC_NS = "http://purl.org/dc/elements/1.1/";

  @Override
  public boolean supports(NewsSourceType source) {
    return source == NewsSourceType.yna;
  }

  @Override
  protected FetchedNews mapItem(Element item) {
    String title = getText(item, "title");
    String link  = getText(item, "link");

    if (title == null || link == null) {
      // 필수 필드 없으면 그냥 스킵
      return null;
    }

    // 2. 작성자: dc:creator 우선, 없으면 author
    String author = getTextNS(item, DC_NS, "creator");
    if (author == null || author.isBlank()) {
      author = getText(item, "author");
    }

    // 3. 요약(overview): description 우선, 없으면 content:encoded 사용
    String overview = getText(item, "description");
    if (overview == null || overview.isBlank()) {
      overview = getTextNS(item, CONTENT_NS, "encoded");
    }
    if (overview != null) {
      overview = overview.trim();
    }

    // 4. 날짜
    LocalDateTime publishedAt = parsePubDate(item, "pubDate");
    return FetchedNews.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(overview.trim())
        .source(NewsSourceType.yna)
        .build();
  }
}