package com.team03.monew.news.service;

import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.dto.NewsDto;
import com.team03.monew.news.dto.NewsResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NewsService {
  NewsResponseDto createNews(NewsCreateRequest newsCreateRequest, UUID interestId);

  void deleteNews_logical(NewsDeleteRequest newsDeleteRequest);

  void deleteNews_physical(NewsDeleteRequest newsDeleteRequest);

  CursorPageResponseArticleDto<NewsDto> findNews(
      String keyword,
      UUID interestId,
      List<NewsSourceType> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String orderBy,
      String direction, //정렬 asc, desc
      String cursor,
      LocalDateTime after, //보조 커서
      int limit
  );

  // 뉴스 단건 조회
  NewsDto getDetailNews(UUID articleId, UUID userId);
}
