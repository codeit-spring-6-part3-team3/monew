package com.team03.monew.web.controller;

import com.team03.monew.web.controller.api.ArticleApi;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleDeleteRequest;
import com.team03.monew.article.dto.ArticleDto;
import com.team03.monew.article.dto.ArticleResponseDto;
import com.team03.monew.article.service.ArticleService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi {

  private final ArticleService articleService;

  // 뉴스 등록
  @PostMapping
  public ResponseEntity<ArticleResponseDto> createArticle(
      @RequestBody ArticleCreateRequest ArticleCreateRequest,
      @RequestParam UUID interestId
      ) {
    ArticleResponseDto response = articleService.createArticle(ArticleCreateRequest, interestId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  // 뉴스 목록 조회
  @GetMapping
  @Override
  public ResponseEntity<CursorPageResponseArticleDto<ArticleDto>> findArticle(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) UUID interestId,
      @RequestParam(required = false) List<ArticleSourceType> sourceIn,
      @RequestParam(required = false) LocalDateTime publishDateFrom,
      @RequestParam(required = false) LocalDateTime publishDateTo,
      @RequestParam(defaultValue = "date") String orderBy,
      @RequestParam(defaultValue = "ASC")String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit
  ) {
    CursorPageResponseArticleDto<ArticleDto> result =  articleService.findArticle(
        keyword,
        interestId,
        sourceIn,
        publishDateFrom,
        publishDateTo,
        orderBy,
        direction,
        cursor,
        after,
        limit
    );
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(result);
  }

  // 뉴스 기사 단편 조회
  @GetMapping("/{articleId}")
  @Override
  public ResponseEntity<ArticleDto> getArticleDetails(
      @PathVariable UUID articleId,
      @RequestParam(required = true) UUID userId
  ){
    ArticleDto dto  = articleService.getDetailArticle(articleId, userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(dto);
  }

  // 논리 삭제
  @DeleteMapping("/{articleId}")
  @Override
  public ResponseEntity<Void> deleteArticleLogical(@PathVariable UUID articleId) {
    articleService.deleteArticle_logical(new ArticleDeleteRequest(articleId));
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  // 물리 삭제
  @DeleteMapping("/{articleId}/hard")
  @Override
  public ResponseEntity<Void> deleteArticlePhysical(@PathVariable UUID articleId) {
    articleService.deleteArticle_physical(new ArticleDeleteRequest(articleId));
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
  public List<Object> restoreArticle(LocalDateTime from, LocalDateTime to) {
    return List.of();
  }

  // 출처 목록 조회
  // 이것은 어떤 기능을 하는지 잘 모르겠습니다.
  // 스웨거 상 작성되어있어 추가 했습니다.
  @GetMapping("/source")
  public ResponseEntity<ArticleSourceType[]> getAllSources() {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ArticleSourceType.values());
  }
}
