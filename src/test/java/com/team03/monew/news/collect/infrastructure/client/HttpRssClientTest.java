package com.team03.monew.news.collect.infrastructure.client;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.collect.infrastructure.parser.HankyungRssParser;
import com.team03.monew.news.collect.infrastructure.parser.RssParser;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpRssClientTest {

  @Mock
  RestTemplate restTemplate;

  @Mock
  RssParser chosunParser;

  @Mock
  RssParser yonhapParser;

  @Mock
  RssParser hangyungRssParser;

  // @InjectMocks 를 쓰려면 HttpRssClient 생성자 시그니처에 맞게 필드/파라미터 이름이 맞아야 함.
  // 상황에 따라 직접 new 로 만들어도 됨.
  HttpRssClient createClient() {
    Map<NewsSourceType, RssParser> parserMap = Map.of(
        NewsSourceType.chosun, chosunParser,
        NewsSourceType.yna, yonhapParser

    );
    // HttpRssClient 생성자 시그니처에 맞게 수정
    return new HttpRssClient(restTemplate, parserMap);
  }

  @Test
  @DisplayName("정상: CHOSUN 소스 요청 시 해당 파서를 사용해 결과를 반환한다")
  void fetch_chosun_success() {
    // given
    HttpRssClient client = createClient();

    String xml = "<rss>...</rss>";
    given(restTemplate.getForObject(anyString(), eq(String.class)))
        .willReturn(xml);

    FetchedNews n1 = new FetchedNews(
        NewsSourceType.chosun,
        "https://www.chosun.com/a",
        "제목1",
        LocalDateTime.now(),
        "개요1"
    );
    FetchedNews n2 = new FetchedNews(
        NewsSourceType.chosun,
        "https://www.chosun.com/b",
        "제목2",
        LocalDateTime.now(),
        "개요2"
    );

    given(chosunParser.parse(xml))
        .willReturn(List.of(n1, n2));

    // when
    List<FetchedNews> result = client.fetch(NewsFeed.CHOSUN_ALL);

    // then
    assertThat(result)
        .hasSize(2)
        .containsExactly(n1, n2);

    // chosunParser만 호출되었는지 확인
    verify(chosunParser).parse(xml);
    verifyNoInteractions(yonhapParser);
  }

  @Test
  @DisplayName("지원하지 않는 소스 요청 시 예외를 던진다")
  void fetch_noParserForSource() {
    // given – parserMap 에 YNA만 넣고 CHOSUN 요청
    Map<NewsSourceType, RssParser> parserMap = Map.of(
        NewsSourceType.yna, yonhapParser
    );
    HttpRssClient client = new HttpRssClient(restTemplate, parserMap);

    // when & then
    assertThatThrownBy(() -> client.fetch(NewsSourceType.chosun))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("chosun");
  }

  @Test
  @DisplayName("RestTemplate 호출 실패 시 예외를 그대로 전파(또는 정책에 맞게 처리)")
  void fetch_httpError() {
    // given
    HttpRssClient client = createClient();

    given(restTemplate.getForObject(anyString(), eq(String.class)))
        .willThrow(new RestClientException("timeout"));

    // when & then
    assertThatThrownBy(() -> client.fetch(NewsSourceType.chosun))
        .isInstanceOf(RestClientException.class);
    // 만약 네가 HttpRssClient 안에서 잡고 빈 리스트를 리턴한다면:
    // List<FetchedNews> result = client.fetch(NewsSourceType.chosun);
    // assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("HTTP 응답 body가 null인 경우 파서에게 전달되며, 그 결과를 그대로 반환한다")
  void fetch_nullBody() {
    // given
    HttpRssClient client = createClient();

    given(restTemplate.getForObject(anyString(), eq(String.class)))
        .willReturn(null);

    given(chosunParser.parse(null))
        .willReturn(List.of());

    // when
    List<FetchedNews> result = client.fetch(NewsSourceType.chosun);

    // then
    assertThat(result).isEmpty();
    verify(chosunParser).parse(null);
  }
}