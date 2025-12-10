package com.team03.monew.news.collect.infrastructure.client;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.collect.exception.RssFetchException;
import com.team03.monew.news.collect.exception.RssParserNotFoundException;
import com.team03.monew.news.collect.infrastructure.parser.RssParser;
import com.team03.monew.news.domain.NewsSourceType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HttpRssClientTest {

  @Mock
  RestTemplate restTemplate;
  @Mock
  RssParser chosunParser;
  @Mock
  RssParser yonhapParser;
  @Mock
  RssParser hankyungParser;

  HttpRssClient clientWithAllParsers() {
    // 기본값은 false, 해당 소스에서만 true
    given(chosunParser.supports(any())).willReturn(false);
    given(yonhapParser.supports(any())).willReturn(false);
    given(hankyungParser.supports(any())).willReturn(false);
    given(chosunParser.supports(NewsSourceType.CHOSUN)).willReturn(true);
    given(yonhapParser.supports(NewsSourceType.YNA)).willReturn(true);
    given(hankyungParser.supports(NewsSourceType.KOREA)).willReturn(true);

    HttpRssClient client = new HttpRssClient(
        restTemplate,
        List.of(chosunParser, yonhapParser, hankyungParser)
    );
    client.initParserMap(); // PostConstruct 대체 호출
    return client;
  }

  @Test
  @DisplayName("정상: CHOSUN 피드 요청 시 해당 파서를 사용해 sink에 전달한다")
  void fetchAndParse_success() {
    HttpRssClient client = clientWithAllParsers();
    String xml = "<rss>...</rss>";
    given(restTemplate.getForObject(anyString(), eq(String.class))).willReturn(xml);

    List<FetchedNews> collected = new ArrayList<>();
    client.fetchAndParse(NewsFeed.CHOSUN_ALL, collected::add);

    verify(chosunParser).parse(eq(xml), any());
    verify(yonhapParser, never()).parse(anyString(), org.mockito.ArgumentMatchers.any());
    verify(hankyungParser, never()).parse(anyString(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("HTTP 요청 실패 시 RssFetchException으로 래핑한다")
  void fetchAndParse_httpError() {
    HttpRssClient client = clientWithAllParsers();
    given(restTemplate.getForObject(anyString(), eq(String.class)))
        .willThrow(new RestClientException("timeout"));

    assertThatThrownBy(() -> client.fetchAndParse(NewsFeed.CHOSUN_ALL, n -> {
    }))
        .isInstanceOf(RssFetchException.class)
        .hasMessageContaining("Failed to fetch RSS");
  }

  @Test
  @DisplayName("응답이 null/blank이면 RssFetchException을 던진다")
  void fetchAndParse_emptyBody() {
    HttpRssClient client = clientWithAllParsers();
    given(restTemplate.getForObject(anyString(), eq(String.class))).willReturn("   ");

    assertThatThrownBy(() -> client.fetchAndParse(NewsFeed.CHOSUN_ALL, n -> {
    }))
        .isInstanceOf(RssFetchException.class)
        .hasMessageContaining("Empty RSS response");
  }

  @Test
  @DisplayName("지원 파서가 없는 경우 초기화 시 RssParserNotFoundException 발생")
  void initParserMap_missingParser() {
    HttpRssClient client = new HttpRssClient(restTemplate, List.of(chosunParser, yonhapParser));

    assertThatThrownBy(client::initParserMap)
        .isInstanceOf(RssParserNotFoundException.class)
        .hasMessageContaining("No RssParser supports press");
  }

  @Test
  @DisplayName("파서에서 예외가 발생하면 그대로 전파된다")
  void fetchAndParse_parserThrows() {
    HttpRssClient client = clientWithAllParsers();
    String xml = "<rss>data</rss>";
    given(restTemplate.getForObject(anyString(), eq(String.class))).willReturn(xml);

    RuntimeException parsingError = new RuntimeException("parse failed");
    doThrow(parsingError).when(chosunParser).parse(eq(xml), any());

    assertThatThrownBy(() -> client.fetchAndParse(NewsFeed.CHOSUN_ALL, n -> {
    })).isSameAs(parsingError);
  }
}
