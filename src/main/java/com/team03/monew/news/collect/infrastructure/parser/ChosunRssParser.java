package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.Press;
//import com.team03.monew.common.dto.FetchedNews;
import org.w3c.dom.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChosunRssParser extends BaseDomRssParser {

  private static final String DC_NS = "http://purl.org/dc/elements/1.1/";
  private static final String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";

//  public ChosunRssParser() {
//    super(Press.CHOSUN);
//  }

  @Override
  public boolean supports(Press press) {
    return press == Press.CHOSUN;
  }

  @Override
  protected FetchedNews mapItem(Element item) {
    String title = getText(item, "title");
    String link = getText(item, "link");

    if (title == null || link == null) {
      return null;
    }

    // 작성자: <dc:creator>
    String author = getTextNS(item, DC_NS, "creator"); // 지금은 안 쓰더라도 파싱만 해둠

    // overview: description 우선, 비어 있으면 content:encoded에서 텍스트 추출
    String overview = getText(item, "description");
    if (overview == null || overview.isBlank()) {
      String encoded = getTextNS(item, CONTENT_NS, "encoded");
      if (encoded != null && !encoded.isBlank()) {
        overview = extractPlainText(encoded);
      }
    }

    LocalDateTime publishedAt = parsePubDate(item, "pubDate");

    return FetchedNews.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(overview != null ? overview.trim() : null)
        .source(Press.CHOSUN)
        .build();
  }

  /**
   * 아주 단순한 HTML → 텍스트 변환 (태그 제거 + 공백 정리)
   */
  private String extractPlainText(String html) {
    return html
        .replaceAll("<[^>]+>", " ")   // 태그 제거
        .replaceAll("\\s+", " ")      // 공백 정리
        .trim();
  }
}