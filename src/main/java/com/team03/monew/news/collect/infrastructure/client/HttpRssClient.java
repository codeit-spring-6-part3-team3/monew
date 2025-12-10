package com.team03.monew.news.collect.infrastructure.client;

import com.team03.monew.news.collect.exception.RssFetchException;
import com.team03.monew.news.collect.infrastructure.parser.RssParser;
import com.team03.monew.news.collect.exception.RssParserNotFoundException;
import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.NewsFeed;
import com.team03.monew.news.domain.NewsSourceType;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpRssClient implements RssClient {

  private final RestTemplate restTemplate;
  private final List<RssParser> parsers;
  private Map<NewsSourceType, RssParser> parserMap;

  @PostConstruct
  void initParserMap() {

    Map<NewsSourceType, RssParser> map = new java.util.HashMap<>();

    for (NewsSourceType source : NewsSourceType.values()) {
      RssParser parser = parsers.stream()
          .filter(p -> p.supports(source))
          .findFirst()
          .orElseThrow(() ->
              new RssParserNotFoundException(source)
          );

      map.put(source, parser);
    }

    this.parserMap = Map.copyOf(map); // 불변 맵으로 고정
  }

  @Override
  public void fetchAndParse(NewsFeed feed, Consumer<FetchedNews> sink) {

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

    parser.parse(xml, sink);
  }

  private RssParser getParser(NewsFeed feed) {
    RssParser parser = parserMap.get(feed.getSource());

    if (parser == null) {
      throw new RssParserNotFoundException(feed);
    }

    return parser;
  }
}