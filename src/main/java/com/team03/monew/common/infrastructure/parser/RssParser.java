package com.team03.monew.common.infrastructure.parser;

import com.team03.monew.common.domain.FetchedNews;
import com.team03.monew.common.domain.Press;
import java.util.List;

public interface RssParser {

  boolean supports(Press press);
  List<FetchedNews> parse(String xml);
}