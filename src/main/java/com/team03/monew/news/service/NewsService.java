package com.team03.monew.news.service;

import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsResponseDto;

public interface NewsService {
  NewsResponseDto createNews(NewsCreateRequest newsCreateRequest);
}
