package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleCollect.domain.FetchedArticles;
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
  public boolean supports(ArticleSourceType source) {
    return source == ArticleSourceType.KOREA;
  }

  @Override
  protected FetchedArticles readItem(XMLStreamReader reader) {
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

          log.trace("[HANKYUNG] START_ELEMENT <{}>", tag);

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
          log.trace("[HANKYUNG] END_ELEMENT </item> → break");
          break;
        }
      }
    } catch (XMLStreamException e) {
      log.warn("[HANKYUNG] XML parsing exception. Skip this item.", e);
      return null;
    }

    log.debug("[HANKYUNG] SUMMARY title='{}', link={}, pubDate={}",
        title, link, publishedAt);

    if (title == null || link == null) {
      log.warn("[HANKYUNG] Skip item: missing title or link. title='{}', link={}", title, link);
      return null;
    }

    FetchedArticles article = FetchedArticles.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(title.trim())
        .source(ArticleSourceType.KOREA)
        .build();

    log.trace("[HANKYUNG] BUILT FetchedArticles = {}", article);

    if (log.isTraceEnabled()) {
      log.trace("[HANKYUNG] readItem END");
    }

    return article;
  }
}
