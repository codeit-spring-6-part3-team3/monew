package com.team03.monew.news.controller;

import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.dto.NewsDto;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.service.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
public class NewsController {

  private final NewsService newsService;

  // 뉴스 등록
  @PostMapping
  public NewsResponseDto createNews(
      @RequestBody NewsCreateRequest newsCreateRequest,
      @RequestParam UUID interestId
      ) {
    return newsService.createNews(newsCreateRequest, interestId);
  }

  // 뉴스 목록 조회
  @GetMapping
  public CursorPageResponseArticleDto<NewsDto> findNews(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) UUID interestId,
      @RequestParam(required = false) List<NewsSourceType> sourceIn,
      @RequestParam(required = false) LocalDateTime publishDateFrom,
      @RequestParam(required = false) LocalDateTime publishDateTo,
      @RequestParam(defaultValue = "date") String orderBy,
      @RequestParam(defaultValue = "ASC")String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit
      ){
    return newsService.findNews(
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
  }

  // 뉴스 기사 단편 조회
  @GetMapping("/{articleId}")
  public NewsDto getNewsDetails(
      @PathVariable UUID articleId,
      @RequestParam(required = true) UUID userId
  ){
    return newsService.getDetailNews(articleId, userId);
  }

  // 논리 삭제
  @DeleteMapping("/{articleId}")
  public void deleteNewsLogical(@PathVariable UUID articleId) {
    newsService.deleteNews_logical(new NewsDeleteRequest(articleId));
  }

  // 물리 삭제
  @DeleteMapping("/{articleId}/hard")
  public void deleteNewsPhysical(@PathVariable UUID articleId) {
    newsService.deleteNews_physical(new NewsDeleteRequest(articleId));
  }
}
