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
public class ChosunRssParser extends StaxRssParser {

  @Override
  public boolean supports(NewsSourceType source) {
    return source == NewsSourceType.CHOSUN;
  }

  @Override
  protected FetchedNews readItem(XMLStreamReader reader) {
    String title = null;
    String link = null;
    String overview = null;
    LocalDateTime publishedAt = null;

    if (log.isTraceEnabled()) {
      log.trace("[CHOSUN] readItem START");
    }

    try {
      while (reader.hasNext()) {
        int eventType = reader.next();

        if (eventType == XMLStreamConstants.START_ELEMENT) {
          String tag = reader.getLocalName();
          String ns = reader.getNamespaceURI();

          log.debug("[CHOSUN] START_ELEMENT <{}> ns={}", tag, ns);

          switch (tag) {
            case "title":
              title = safeElementText(reader);
              break;

            case "link":
              link = safeElementText(reader);
              break;

            case "description":
              overview = safeElementText(reader);
              break;

            case "encoded":
              if (CONTENT_NS.equals(ns)) {
                String encoded = safeElementText(reader);
                log.debug("[CHOSUN] encoded len={}", encoded != null ? encoded.length() : 0);

                String alt = extractImgAlt(encoded);
                if (alt != null && !alt.isBlank()) {
                  overview = alt.trim();
                }

                String pText = extractFirstPText(encoded);
                if (pText != null && !pText.isBlank()) {
                  overview = pText.trim();
                }
              }
              break;

            case "pubDate":
              String pubDateText = safeElementText(reader);
              publishedAt = parsePubDate(pubDateText);
              break;
          }

        } else if (eventType == XMLStreamConstants.END_ELEMENT) {
          if ("item".equals(reader.getLocalName())) {
            log.debug("[CHOSUN] END_ELEMENT </item> → break");
            break;
          }
        }
      }
    } catch (XMLStreamException e) {
      log.warn("[CHOSUN] XML parsing exception. Skip this item.", e);
      return null;
    }

    log.info("[CHOSUN] SUMMARY title='{}', link={}, pubDate={}, overview={}",
        title, link, publishedAt, preview(overview, 80));

    if (title == null || link == null) {
      log.warn("[CHOSUN] Skip item: missing title or link. title='{}', link={}", title, link);
      return null;
    }

    FetchedNews news = FetchedNews.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(overview != null ? overview.trim() : null)
        .source(NewsSourceType.CHOSUN)
        .build();

    log.debug("[CHOSUN] BUILT FetchedNews = {}", news);

    if (log.isTraceEnabled()) {
      log.trace("[CHOSUN] readItem END");
    }

    return news;
  }

  private String extractFirstPText(String html) {
    if (html == null) {
      return null;
    }

    String lower = html.toLowerCase();
    int start = lower.indexOf("<p");
    if (start == -1) {
      return null;
    }

    int tagEnd = lower.indexOf(">", start);
    if (tagEnd == -1) {
      return null;
    }

    int contentStart = tagEnd + 1;
    int endIdx = lower.indexOf("</p>", contentStart);
    if (endIdx == -1) {
      return null;
    }

    return html.substring(contentStart, endIdx).trim();
  }

  private String extractImgAlt(String html) {
    if (html == null) {
      return null;
    }

    String lower = html.toLowerCase();
    int imgPos = lower.indexOf("<img");
    if (imgPos == -1) {
      return null;
    }

    int altPos = lower.indexOf("alt=\"", imgPos);
    if (altPos == -1) {
      return null;
    }

    int start = altPos + "alt=\"".length();
    int end = html.indexOf("\"", start);
    if (end == -1) {
      return null;
    }

    return html.substring(start, end).trim();
  }
}