package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.domain.NewsSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.time.LocalDateTime;

@Slf4j
@Component
public class HankyungRssParser extends StaxRssParser {

  @Override
  public boolean supports(NewsSourceType source) {
    return source == NewsSourceType.KOREA;
  }

  @Override
  protected FetchedNews readItem(XMLStreamReader reader) {
    String title = null;
    String link = null;
    LocalDateTime publishedAt = null;

    if (log.isTraceEnabled()) {
      log.trace("[HANKYUNG] readItem START");
    }

    try {
      while (reader.hasNext()) {
        int eventType = reader.next();

        if (eventType == XMLStreamConstants.START_ELEMENT) {
          String tag = reader.getLocalName();

          log.debug("[HANKYUNG] START_ELEMENT <{}>", tag);

          switch (tag) {
            case "title":
              title = safeElementText(reader);
              break;

            case "link":
              link = safeElementText(reader);
              break;

            case "pubDate":
              String pubDateText = safeElementText(reader);
              publishedAt = parsePubDate(pubDateText);
              break;
          }
        }

        if (eventType == XMLStreamConstants.END_ELEMENT &&
            "item".equals(reader.getLocalName())) {
          log.debug("[HANKYUNG] END_ELEMENT </item> → break");
          break;
        }
      }
    } catch (XMLStreamException e) {
      log.warn("[HANKYUNG] XML parsing exception. Skip this item.", e);
      return null;
    }

    log.info("[HANKYUNG] SUMMARY title='{}', link={}, pubDate={}",
        title, link, publishedAt);

    if (title == null || link == null) {
      log.warn("[HANKYUNG] Skip item: missing title or link. title='{}', link={}", title, link);
      return null;
    }

    FetchedNews news = FetchedNews.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(title.trim())
        .source(NewsSourceType.KOREA)
        .build();

    log.debug("[HANKYUNG] BUILT FetchedNews = {}", news);

    if (log.isTraceEnabled()) {
      log.trace("[HANKYUNG] readItem END");
    }

    return news;
  }
}