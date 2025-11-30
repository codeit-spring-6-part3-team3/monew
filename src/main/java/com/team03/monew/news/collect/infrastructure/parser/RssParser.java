package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.collect.domain.Press;
import java.util.List;

public interface RssParser {

  boolean supports(Press press);
  List<FetchedNews> parse(String xml);
}