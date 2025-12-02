package com.team03.monew.news.service;

import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface NewsService {
  NewsResponseDto createNews(NewsCreateRequest newsCreateRequest);

  void deleteNews_logical(NewsDeleteRequest newsDeleteRequest);

  void deleteNews_physical(NewsDeleteRequest newsDeleteRequest);

}
