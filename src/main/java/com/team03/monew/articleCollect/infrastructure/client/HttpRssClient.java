package com.team03.monew.articleCollect.infrastructure.client;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.articleCollect.exception.RssFetchException;
import com.team03.monew.articleCollect.infrastructure.parser.RssParser;
import com.team03.monew.articleCollect.exception.RssParserNotFoundException;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import com.team03.monew.articleCollect.domain.ArticlesFeed;
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
  private Map<ArticleSourceType, RssParser> parserMap;

  @PostConstruct
  void initParserMap() {

    Map<ArticleSourceType, RssParser> map = new java.util.HashMap<>();

    for (ArticleSourceType source : ArticleSourceType.values()) {
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
  public void fetchAndParse(ArticlesFeed feed, Consumer<FetchedArticles> sink) {

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

  private RssParser getParser(ArticlesFeed feed) {
    RssParser parser = parserMap.get(feed.getSource());

    if (parser == null) {
      throw new RssParserNotFoundException(feed);
    }

    return parser;
  }
}