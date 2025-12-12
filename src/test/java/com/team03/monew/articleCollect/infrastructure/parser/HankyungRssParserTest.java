package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.article.domain.ArticleSourceType;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class HankyungRssParserTest {

  private XMLStreamReader createReader(String itemXml) throws Exception {
    String xml = "<rss><channel>" + itemXml + "</channel></rss>";
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));

    while (reader.hasNext()) {
      int eventType = reader.next();
      if (eventType == XMLStreamConstants.START_ELEMENT &&
          "item".equals(reader.getLocalName())) {
        break;
      }
    }
    return reader;
  }

  @Test
  void 정상_아이템_파싱() throws Exception {
    String itemXml = """
            <item>
              <title>테스트 기사 제목</title>
              <link>https://www.hankyung.com/news/123</link>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    HankyungRssParser parser = new HankyungRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("테스트 기사 제목", news.title());
    assertEquals("https://www.hankyung.com/news/123", news.resourceLink());
    assertNotNull(news.postDate());
    // Hankyung 은 overview 를 title 로 사용
    assertEquals("테스트 기사 제목", news.overview());
    assertEquals(ArticleSourceType.KOREA, news.source());
  }

  @Test
  void 타이틀_없으면_null_리턴() throws Exception {
    String itemXml = """
            <item>
              <link>https://www.hankyung.com/news/123</link>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    HankyungRssParser parser = new HankyungRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNull(news);
  }

  @Test
  void 링크_없으면_null_리턴() throws Exception {
    String itemXml = """
            <item>
              <title>테스트 기사 제목</title>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    HankyungRssParser parser = new HankyungRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNull(news);
  }

  @Test
  void pubDate가_이상해도_title_link_있으면_뉴스는_만든다() throws Exception {
    String itemXml = """
            <item>
              <title>테스트 기사 제목</title>
              <link>https://www.hankyung.com/news/123</link>
              <pubDate>INVALID_DATE</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    HankyungRssParser parser = new HankyungRssParser();

    FetchedArticles news = parser.readItem(reader);

    // parsePubDate 구현에 따라 다를 수 있지만,
    // 현재 코드는 title/link만 체크하므로 null 이 아니어야 함
    assertNotNull(news);
    assertEquals("테스트 기사 제목", news.title());
    assertEquals("https://www.hankyung.com/news/123", news.resourceLink());
    // postDate 가 null 일 수 있으니, 이건 그냥 허용
  }
}
