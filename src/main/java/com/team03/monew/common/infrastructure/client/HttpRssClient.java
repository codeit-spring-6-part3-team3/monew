package com.team03.monew.common.infrastructure.client;

import com.team03.monew.common.exception.RssFetchException;
import com.team03.monew.common.infrastructure.parser.RssParser;
import com.team03.monew.common.exception.RssParserNotFoundException;
import com.team03.monew.common.domain.FetchedNews;
import com.team03.monew.common.domain.NewsFeed;
import com.team03.monew.common.domain.Press;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpRssClient implements RssClient {

  private final RestTemplate restTemplate;
  private final List<RssParser> parsers;
  private Map<Press, RssParser> parserMap;

  @PostConstruct
  void initParserMap() {

    Map<Press, RssParser> map = new java.util.HashMap<>();

    for (Press press : Press.values()) {
      RssParser parser = parsers.stream()
          .filter(p -> p.supports(press))
          .findFirst()
          .orElseThrow(() ->
              new IllegalStateException("No RssParser supports press: " + press)
          );

      map.put(press, parser);
    }

    this.parserMap = Map.copyOf(map); // 불변 맵으로 고정
  }

  @Override
  public List<FetchedNews> fetch(NewsFeed feed) {

    RssParser parser = getParser(feed);

    final String xml;

    try {
      xml = restTemplate.getForObject(feed.getUrl(), String.class);
    } catch (Exception e) {
      throw new RssFetchException(feed,
          "Failed to fetch RSS from " + feed.getUrl(), e);
    }

    if (xml == null || xml.isBlank()) {
      throw new RssFetchException(feed,
          "Empty RSS response from " + feed.getUrl(), null);
    }

    return parser.parse(xml);
  }

  private RssParser getParser(NewsFeed feed) {
    RssParser parser = parserMap.get(feed.getPress());

    if (parser == null) {
      throw new RssParserNotFoundException(feed);
    }

    return parser;
  }
}