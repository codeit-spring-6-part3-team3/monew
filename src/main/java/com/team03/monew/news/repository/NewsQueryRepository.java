package com.team03.monew.news.repository;

import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NewsQueryRepository {
  CursorPageResponseArticleDto<NewsDto> searchNews(
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
}
