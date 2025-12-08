package com.team03.monew.news.controller;

import com.team03.monew.news.controller.api.NewsApi;
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
public class NewsController implements NewsApi {

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
  @Override
  public CursorPageResponseArticleDto<NewsDto> findNews(
      String keyword,
      UUID interestId,
      List<NewsSourceType> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
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
  @Override
  public NewsDto getNewsDetails(
      @PathVariable UUID articleId,
      @RequestParam(required = true) UUID userId
  ){
    return newsService.getDetailNews(articleId, userId);
  }

  // 논리 삭제
  @DeleteMapping("/{articleId}")
  @Override
  public void deleteNewsLogical(@PathVariable UUID articleId) {
    newsService.deleteNews_logical(new NewsDeleteRequest(articleId));
  }

  // 물리 삭제
  @DeleteMapping("/{articleId}/hard")
  @Override
  public void deleteNewsPhysical(@PathVariable UUID articleId) {
    newsService.deleteNews_physical(new NewsDeleteRequest(articleId));
  }

  @Override
  public List<Object> restoreNews(LocalDateTime from, LocalDateTime to) {
    return List.of();
  }

  // 출처 목록 조회
  // 이것은 어떤 기능을 하는지 잘 모르겠습니다.
  // 스웨거 상 작성되어있어 추가 했습니다.
  @GetMapping("/source")
  public NewsSourceType[] getAllSources() {
    return NewsSourceType.values();
  }
}
