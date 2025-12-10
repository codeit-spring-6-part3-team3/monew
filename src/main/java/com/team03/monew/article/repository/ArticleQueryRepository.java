package com.team03.monew.article.repository;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ArticleQueryRepository {
  CursorPageResponseArticleDto<ArticleDto> searchArticles(
      String keyword,
      UUID interestId,
      List<ArticleSourceType> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String orderBy,
      String direction, //정렬 asc, desc
      String cursor,
      LocalDateTime after, //보조 커서
      int limit
  );
}
