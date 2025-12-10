package com.team03.monew.news.collect.mapper;

import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.dto.NewsCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {
  public NewsCreateRequest toCreateRequest(FetchedNews fetched) {
    return new NewsCreateRequest(
        fetched.source(),      // enum
        fetched.resourceLink(),        // resourceLink
        fetched.title(),       // title
        fetched.postDate(),    // postDate (null이면 서비스에서 처리하게 둘 수도)
        fetched.overview()     // overView
    );
  }
}
