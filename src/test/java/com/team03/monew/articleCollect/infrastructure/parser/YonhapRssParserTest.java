package com.team03.monew.articleCollect.infrastructure.parser;

import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.article.domain.ArticleSourceType;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class YonhapRssParserTest {

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
              <title>연합뉴스 기사</title>
              <link>https://www.yonhapnewstv.co.kr/news/MYID</link>
              <description>요약 내용 A</description>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    YonhapRssParser parser = new YonhapRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    assertEquals("요약 내용 A", news.overview());
    assertEquals(ArticleSourceType.YNA, news.source());
  }

  @Test
  void description이_비어있고_encoded가_있으면_encoded_클린텍스트가_overview() throws Exception {
    String itemXml = """
            <item>
              <title>연합뉴스 기사</title>
              <link>https://www.yonhapnewstv.co.kr/news/MYID</link>
              <description>   </description>
              <content:encoded xmlns:content="http://purl.org/rss/1.0/modules/content/">
                <![CDATA[
                  <p>요약 <b>내용</b> 입니다.</p>
                ]]>
              </content:encoded>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    YonhapRssParser parser = new YonhapRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    // cleanHtmlText 결과: "요약 내용 입니다."
    assertEquals("요약 내용 입니다.", news.overview());
  }

  @Test
  void description과_encoded가_같이_있으면_description_우선() throws Exception {
    String itemXml = """
            <item>
              <title>연합뉴스 기사</title>
              <link>https://www.yonhapnewstv.co.kr/news/MYID</link>
              <description>짧은 요약</description>
              <content:encoded xmlns:content="http://purl.org/rss/1.0/modules/content/">
                <![CDATA[
                  <p>긴 본문 내용...</p>
                ]]>
              </content:encoded>
              <pubDate>Tue, 02 Dec 2025 10:00:00 +0900</pubDate>
            </item>
            """;

    XMLStreamReader reader = createReader(itemXml);
    YonhapRssParser parser = new YonhapRssParser();

    FetchedArticles news = parser.readItem(reader);

    assertNotNull(news);
    // description 이 이미 있으므로 encoded 로 덮어쓰지 않음
    assertEquals("짧은 요약", news.overview());
  }

  @Test
  void title이나_link가_없으면_null() throws Exception {
    String noTitle = """
            <item>
              <link>https://www.yonhapnewstv.co.kr/news/MYID</link>
            </item>
            """;

    XMLStreamReader r1 = createReader(noTitle);
    YonhapRssParser parser = new YonhapRssParser();
    assertNull(parser.readItem(r1));

    String noLink = """
            <item>
              <title>연합뉴스 기사</title>
            </item>
            """;

    XMLStreamReader r2 = createReader(noLink);
    assertNull(parser.readItem(r2));
  }
}
