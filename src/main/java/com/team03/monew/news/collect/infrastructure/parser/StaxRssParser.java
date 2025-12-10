package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

public abstract class StaxRssParser implements RssParser {

  protected static final String DC_NS = "http://purl.org/dc/elements/1.1/";
  protected static final String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";

  private static final DateTimeFormatter PUB_DATE_FORMATTER =
      DateTimeFormatter.RFC_1123_DATE_TIME;

  private static final ZoneId TARGET_ZONE = ZoneId.of("Asia/Seoul");

  public void parse(String xml, Consumer<FetchedNews> sink) {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = null;

    try {
      reader = factory.createXMLStreamReader(new StringReader(xml));

      while (reader.hasNext()) {
        int eventType = reader.next();

        if (eventType != XMLStreamConstants.START_ELEMENT) {
          continue;
        }

        String tag = reader.getLocalName();

        if (tag.equals(getItemElementName())) {
          FetchedNews news = readItem(reader);
          if (news != null) {
            sink.accept(news);
          }
        }
      }

    } catch (XMLStreamException e) {
      // 여기서 try-catch로 한 번 감싸서 도메인 예외 or 런타임으로 래핑
      throw new RuntimeException("Failed to parse RSS XML with StAX", e);
      // 필요하면 커스텀 예외로:
      // throw new RssParseException("Failed to parse RSS XML", e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (XMLStreamException ignored) {
          // 로깅만 하고 무시해도 됨
        }
      }
    }
  }

  protected abstract FetchedNews readItem(XMLStreamReader reader);

  protected String preview(String text, int maxLen) {
    if (text == null) return null;
    text = text.trim();
    if (text.length() <= maxLen) return text;
    return text.substring(0, maxLen) + "... (len=" + text.length() + ")";
  }

  // MARK: 신문사마다 다른 xml의 뉴스 헤더에 맞게 수정 필요
  protected String getItemElementName() {
    return "item";
  }

  protected String safeElementText(XMLStreamReader reader) throws XMLStreamException {
    return reader.getElementText();
  }

  protected LocalDateTime parsePubDate(String pubDateText) {
    if (pubDateText == null || pubDateText.isBlank()) {
      return null;
    }

    try {
      ZonedDateTime zdt = ZonedDateTime.parse(pubDateText.trim(), PUB_DATE_FORMATTER);
      return zdt.withZoneSameInstant(TARGET_ZONE).toLocalDateTime();
    } catch (Exception e) {
      // 실패하면 null 반환 (정책은 추후 결정 가능)
      return null;
    }
  }
}