package com.team03.monew.news.service;

import com.team03.monew.articleView.service.NewsViewsService;
import com.team03.monew.news.domain.News;
import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.dto.NewsDto;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.exception.CustomException.NewsCanNotDelete;
import com.team03.monew.news.exception.CustomException.NewsNotFound;
import com.team03.monew.news.exception.CustomException.SameResourceLink;
import com.team03.monew.news.repository.NewsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Service
public class BasicNewsService implements NewsService {

  private final NewsRepository newsRepository;
  private final NewsViewsService newsViewsService;

  @Override
  public CursorPageResponseArticleDto<NewsDto> findNews(
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
  ) {
    return newsRepository.searchNews(
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


  //뉴스 저장(등록)
  @Transactional
  @Override
  public NewsResponseDto createNews(NewsCreateRequest newsCreateRequest) {

    Optional<News> existing = newsRepository.findByResourceLink(newsCreateRequest.resourceLink());
    if(existing.isPresent()) {
      throw new SameResourceLink();
    }
    //엔티티
    News news = News.builder()
        .source(newsCreateRequest.source())
        .resourceLink(newsCreateRequest.resourceLink())
        .title(newsCreateRequest.title())
        .postDate(newsCreateRequest.postDate())
        .overview(newsCreateRequest.overView())
        .build();

    //저장
    News savedNews = newsRepository.save(news);

    //dto변환
    return NewsResponseDto.from(savedNews);
  }

  // 뉴스 삭제(논리)
  @Transactional
  @Override
  public void deleteNews_logical(NewsDeleteRequest newsDeleteRequest) {
    News news = newsRepository.findById(newsDeleteRequest.articleId())
        .orElseThrow(NewsNotFound::new);

    news.deleteNews();
    newsRepository.save(news);
  }

  //뉴스 삭제(물리)
  @Transactional
  @Override
  public void deleteNews_physical(NewsDeleteRequest newsDeleteRequest) {
    News news = newsRepository.findById(newsDeleteRequest.articleId())
        .orElseThrow(NewsNotFound::new);

    if(!news.isDelete()){
      throw new NewsCanNotDelete();
    }
    newsRepository.delete(news);
  }

  // 뉴스 단건 조회
  @Override
  public NewsDto getDetailNews(UUID articleId, UUID userId) {

    // 뉴스 기사 없을시 에러 처리
    News news =  newsRepository.findById(articleId)
        .orElseThrow(NewsNotFound::new);

    //초기 읽은 여부
    boolean viewedByMe = newsViewsService.isRead(news,userId);

    return NewsDto.from(news,viewedByMe);
  }
}