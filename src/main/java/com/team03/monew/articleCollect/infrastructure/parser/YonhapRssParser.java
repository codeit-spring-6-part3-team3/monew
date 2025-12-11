package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import java.time.LocalDateTime;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class YonhapRssParser extends StaxRssParser {

  @Override
  public boolean supports(ArticleSourceType source) {
    return source == ArticleSourceType.YNA;
  }

  @Override
  protected FetchedArticles readItem(XMLStreamReader reader) {

    String title = null;
    String link = null;
    String overview = null;
    LocalDateTime publishedAt = null;

    if (log.isTraceEnabled()) {
      log.trace("[YNA] readItem START");
    }

    try {
      while (reader.hasNext()) {
        int eventType = reader.next();

        if (eventType == XMLStreamConstants.START_ELEMENT) {
          String tag = reader.getLocalName();
          String ns = reader.getNamespaceURI();

          log.trace("[YNA] START_ELEMENT <{}> ns={}", tag, ns);

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
                log.trace("[YNA] encoded len={}", encoded != null ? encoded.length() : 0);

                if ((overview == null || overview.isBlank()) && encoded != null) {
                  overview = cleanHtmlText(encoded);
                }
              }
              break;

            case "pubDate":
              String pubTxt = safeElementText(reader);
              publishedAt = parsePubDate(pubTxt);
              break;

            default:
              log.trace("[YNA] ignore tag <{}>", tag);
              break;
          }
        } else if (eventType == XMLStreamConstants.END_ELEMENT) {
          if ("item".equals(reader.getLocalName())) {
            log.trace("[YNA] END_ELEMENT </item> → break");
            break;
          }
        }
      }
    } catch (XMLStreamException e) {
      log.warn("[YNA] XML parsing exception. Skip this item.", e);
      return null;
    }

    log.debug("[YNA] SUMMARY title='{}', link={}, pubDate={}, overview={}",
        title, link, publishedAt, preview(overview, 80));

    if (title == null || link == null) {
      log.warn("[YNA] Skip item: missing title or link. title='{}', link={}", title, link);
      return null;
    }

    FetchedArticles article = FetchedArticles.builder()
        .title(title.trim())
        .resourceLink(link.trim())
        .postDate(publishedAt)
        .overview(overview != null ? overview.trim() : null)
        .source(ArticleSourceType.YNA)
        .build();

    log.trace("[YNA] BUILT FetchedArticles = {}", article);

    if (log.isTraceEnabled()) {
      log.trace("[YNA] readItem END");
    }

    return article;
  }

  private String cleanHtmlText(String html) {
    if (html == null) {
      return null;
    }
    String noTags = html.replaceAll("<[^>]+>", " ");
    String normalized = noTags.replaceAll("\\s+", " ").trim();
    return normalized.isEmpty() ? null : normalized;
  }
}
