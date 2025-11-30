package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

@Slf4j
public abstract class BaseDomRssParser implements RssParser {

  private static final DateTimeFormatter PUB_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

  @Override
  public List<FetchedNews> parse(String xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(xml)));

      NodeList items = doc.getElementsByTagName("item");
      List<FetchedNews> result = new ArrayList<>();

      for (int i = 0; i < items.getLength(); i++) {
        Element item = (Element) items.item(i);
        FetchedNews news = mapItem(item);
        if (news != null) {
          result.add(news);
        }
      }

      return result;
    } catch (Exception e) {

      throw new RuntimeException(e);
//      throw new RssParseException(feed, "Failed to parse RSS XML", e);
    }
  }

  protected abstract FetchedNews mapItem(Element item);

  // 공통 유틸들

  protected String getText(Element parent, String tagName) {
    NodeList list = parent.getElementsByTagName(tagName);
    if (list == null || list.getLength() == 0) {
      return null;
    }
    Node node = list.item(0);
    return node != null ? node.getTextContent() : null;
  }

  protected String getTextNS(Element parent, String namespaceUri, String localName) {
    NodeList list = parent.getElementsByTagNameNS(namespaceUri, localName);
    if (list == null || list.getLength() == 0) {
      return null;
    }
    Node node = list.item(0);
    return node != null ? node.getTextContent() : null;
  }

  protected LocalDateTime parsePubDate(Element item, String tagName) {
    String raw = getText(item, tagName);
    if (raw == null || raw.isBlank()) {
      return null;
    }

    try {
      ZonedDateTime zoned = ZonedDateTime.parse(raw.trim(), PUB_DATE_FORMATTER)
          .withZoneSameInstant(ZoneId.of("Asia/Seoul"));

      return zoned.toLocalDateTime();

    } catch (Exception e) {
      log.warn("Failed to parse pubDate: {}", raw, e);
      return null;
    }
  }
}