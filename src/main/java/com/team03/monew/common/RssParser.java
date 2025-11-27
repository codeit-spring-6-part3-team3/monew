package com.team03.monew.common;

import com.team03.monew.common.domain.NewsFeed;
import com.team03.monew.common.domain.Press;
import java.util.List;

public interface RssParser {

  boolean supports(Press press);
  List<FetchedNews> parse(String xml, NewsFeed feed);
}