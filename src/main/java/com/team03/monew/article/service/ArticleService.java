package com.team03.monew.article.service;

import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleDeleteRequest;
import com.team03.monew.article.dto.ArticleDto;
import com.team03.monew.article.dto.ArticleResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
  ArticleResponseDto createArticle(ArticleCreateRequest articleCreateRequest, UUID interestId);

  void deleteArticle_logical(ArticleDeleteRequest articleDeleteRequest);

  void deleteArticle_physical(ArticleDeleteRequest articleDeleteRequest);

  CursorPageResponseArticleDto<ArticleDto> findArticle(
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

  // 뉴스 단건 조회
  ArticleDto getDetailArticle(UUID articleId, UUID userId);

  ArticleSourceType[] getAllSources();
}
