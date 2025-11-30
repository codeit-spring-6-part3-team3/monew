//package com.team03.monew.common;
//
//import com.team03.monew.common.domain.Press;
//import java.util.List;
//
//public class HankyungRssParser implements RssParser{
//
//  @Override
//  public boolean supports(Press press) {
//    return press == Press.HANKYUNG;
//  }
//
//  @Override
//  public List<FetchedNews> parse(String xml) {
//    return List.of();
//  }
//}

package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class HankyungRssParser extends BaseDomRssParser {

  @Override
  public boolean supports(NewsSourceType source) {
    return source == NewsSourceType.korea;
  }

  @Override
  protected FetchedNews mapItem(Element item) {
    String title = getText(item, "title");
    String link = getText(item, "link");
    String author = getText(item, "author"); // 없으면 null
//    String overview = getText(item, "overview");

    if (title == null || link == null) {
      return null;
    }

    LocalDateTime publishedAt = parsePubDate(item, "pubDate");

    return FetchedNews.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .source(NewsSourceType.korea)
        .build();
  }
}