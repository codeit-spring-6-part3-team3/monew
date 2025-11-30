package com.team03.monew.news.collect.infrastructure.parser;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.domain.NewsSourceType;
import java.util.List;

public interface RssParser {

  boolean supports(NewsSourceType source);
  List<FetchedNews> parse(String xml);
}