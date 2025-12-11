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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi {

  private final ArticleService articleService;

  // 뉴스 등록
  @PostMapping
  public ResponseEntity<ArticleResponseDto> createArticle(
      @RequestBody ArticleCreateRequest articleCreateRequest,
      @RequestParam UUID interestId
      ) {

    log.info("POST /api/articles 뉴스 등록 요청. title : {}, interestId : {}, resourceLink : {}"
        ,articleCreateRequest.title(),interestId,articleCreateRequest.resourceLink());

        ArticleResponseDto response = articleService.createArticle(articleCreateRequest, interestId);

    log.info("POST /api/articles 뉴스 등록 응답. created : {}", response != null);

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
      @RequestParam(defaultValue = "publishDate") String orderBy,
      @RequestParam(defaultValue = "ASC") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {

    log.debug("GET /api/articles 뉴스 목록 조회 요청. orderBy : {}, direction : {}",  orderBy, direction);

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

    log.debug("GET /api/articles 뉴스 목록 조회 응답. size={}, hasNext={}, nextCursor={}",
        result.content().size(),
        result.hasNext(),
        result.nextCursor()
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
      @RequestHeader("Monew-Request-User-ID") UUID userId

  ){

    log.info("GET /api/articles/{} 뉴스 단편 조회 요청. userId : {}",articleId, userId);

    ArticleDto dto  = articleService.getDetailArticle(articleId, userId);

    log.info("GET /api/articles/{} 뉴스 단편 조회 응답. viewedByMe={}", articleId, dto.viewedByMe());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(dto);
  }

  // 논리 삭제
  @DeleteMapping("/{articleId}")
  @Override
  public ResponseEntity<Void> deleteArticleLogical(@PathVariable UUID articleId) {

    log.info("DELETE /api/articles/{} 논리 삭제 요청", articleId);

    articleService.deleteArticle_logical(new ArticleDeleteRequest(articleId));

    log.info("DELETE /api/articles/{} 논리 삭제 응답.", articleId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  // 물리 삭제
  @DeleteMapping("/{articleId}/hard")
  @Override
  public ResponseEntity<Void> deleteArticlePhysical(@PathVariable UUID articleId) {

    log.info("DELETE /api/articles/{}/hard 뉴스 물리 삭제 요청", articleId);

    articleService.deleteArticle_physical(new ArticleDeleteRequest(articleId));

    log.info("DELETE /api/articles/{}/hard 뉴스 물리 삭제 응답.", articleId);

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
  @GetMapping("/sources")
  public ResponseEntity<ArticleSourceType[]> getAllSources() {

    log.debug("GET /api/articles/source");

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ArticleSourceType.values());
  }
}
