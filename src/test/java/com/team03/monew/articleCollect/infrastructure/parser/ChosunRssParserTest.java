package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.article.domain.ArticleSourceType;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ChosunRssParserTest {

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
  void description만_있으면_overview는_description() throws Exception {
    String itemXml = """
            <item>
              <title>조선 기사</title>
              <link>https://www.chosun.com/article1</link>
              <description>요약 내용입니다.</description>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    ChosunRssParser parser = new ChosunRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("조선 기사", news.title());
    assertEquals("요약 내용입니다.", news.overview());
    assertEquals(ArticleSourceType.CHOSUN, news.source());
  }

  @Test
  void encoded에_alt만_있으면_alt가_overview() throws Exception {
    String itemXml = """
            <item>
              <title>조선 기사</title>
              <link>https://www.chosun.com/article1</link>
              <content:encoded xmlns:content="http://purl.org/rss/1.0/modules/content/">
                <![CDATA[
                  <img src="x.jpg" alt="이미지 설명입니다">
                ]]>
              </content:encoded>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    ChosunRssParser parser = new ChosunRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("이미지 설명입니다", news.overview());
  }

  @Test
  void encoded에_alt와_p가_있으면_p텍스트가_최종_overview() throws Exception {
    String itemXml = """
            <item>
              <title>조선 기사</title>
              <link>https://www.chosun.com/article1</link>
              <content:encoded xmlns:content="http://purl.org/rss/1.0/modules/content/">
                <![CDATA[
                  <img src="x.jpg" alt="이미지 설명입니다">
                  <p>본문 첫 문장입니다.</p>
                ]]>
              </content:encoded>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    ChosunRssParser parser = new ChosunRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("본문 첫 문장입니다.", news.overview());
  }

  @Test
  void description과_encoded가_같이_있으면_encoded가_description을_덮어쓴다() throws Exception {
    String itemXml = """
            <item>
              <title>조선 기사</title>
              <link>https://www.chosun.com/article1</link>
              <description>설명 A</description>
              <content:encoded xmlns:content="http://purl.org/rss/1.0/modules/content/">
                <![CDATA[
                  <p>설명 B</p>
                ]]>
              </content:encoded>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    ChosunRssParser parser = new ChosunRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("설명 B", news.overview());
  }

  @Test
  void title이나_link가_없으면_null() throws Exception {
    String itemWithoutTitle = """
            <item>
              <link>https://www.chosun.com/article1</link>
              <description>설명</description>
            </item>
            """;

    XMLStreamReader r1 = createReader(itemWithoutTitle);
    ChosunRssParser parser = new ChosunRssParser();
    assertNull(parser.readItem(r1));

    String itemWithoutLink = """
            <item>
              <title>조선 기사</title>
              <description>설명</description>
            </item>
            """;

    XMLStreamReader r2 = createReader(itemWithoutLink);
    assertNull(parser.readItem(r2));
  }
}
