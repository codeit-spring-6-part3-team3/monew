package com.team03.monew.web.controller;

import com.team03.monew.article.domain.Article;
import com.team03.monew.articleviews.dto.ArticleViewDto;
import com.team03.monew.articleviews.service.ArticleViewsService;
import com.team03.monew.web.controller.api.ArticleViewApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleViewController implements ArticleViewApi {

  private final ArticleViewsService articleViewsService;

  @PostMapping("/{articleId}/article-views")
  @Override
  public ResponseEntity<ArticleViewDto> register(
      @PathVariable UUID articleId,
      @RequestHeader("Monew-Request-User-ID") UUID viewedBy
  ) {

    log.info("기사 뷰 등록 요청. articleId: {}, viewedBy: {}", articleId, viewedBy);

    ArticleViewDto response = articleViewsService.registerArticleViews(articleId,viewedBy);

    log.info("기사 뷰 등록 응답. articleId: {}, viewedBy: {}, viewRecordId: {}",
        articleId, viewedBy, response.id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .build();

  }
}
