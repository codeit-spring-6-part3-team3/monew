package com.team03.monew.common;

import com.team03.monew.common.domain.NewsFeed;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpRssClient implements RssClient {

  private final RestTemplate restTemplate;
  private final List<RssParser> parsers;

  @Override
  public List<FetchedNews> fetch(NewsFeed feed) {

    RssParser parser = parsers.stream()
        .filter(p -> p.supports(feed.getPress()))
        .findFirst()
        .orElseThrow(() -> new RssParserNotFoundException(feed));

    final String xml;

    try {
      xml = restTemplate.getForObject(feed.getUrl(), String.class);
    } catch (RssFetchException e) {
      throw new RssFetchException(feed,
          "Failed to fetch RSS from " + feed.getUrl(), e);
    }

    if (xml == null || xml.isBlank()) {
      throw new RssFetchException(feed,
          "Empty RSS response from " + feed.getUrl(), null);
    }

    return parser.parse(xml, feed);
  }
}