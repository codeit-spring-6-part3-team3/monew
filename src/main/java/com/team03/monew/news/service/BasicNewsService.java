package com.team03.monew.news.service;

import com.team03.monew.articleView.service.NewsViewsService;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
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
  private final InterestRepository interestRepository;

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


  // 뉴스 제목, 요약에 특정 내용이 있는지 확인하는 메서드
  private boolean containText(NewsCreateRequest newsCreateRequest, String text) {
    return newsCreateRequest.title().contains(text) || newsCreateRequest.overView().contains(text);
  }

  //뉴스 저장(등록)
  @Transactional
  @Override
  public NewsResponseDto createNews(NewsCreateRequest newsCreateRequest, UUID interestId) {

    Optional<News> existing = newsRepository.findByResourceLink(newsCreateRequest.resourceLink());
    if(existing.isPresent()) {
      throw new SameResourceLink();
    }

    //관심사 조회
    Interest interest = interestRepository.findById(interestId)
        // 관심사 없을떄 예외 발생
        .orElseThrow(() -> new RuntimeException());

    //수집한 기사 중 관심사의 키워드를 포함하는 뉴스 기사만 저장합니다.
    boolean matchInterest = containText(newsCreateRequest, interest.getName()) || interest.getKeywords().stream()
        .anyMatch(keyword -> containText(newsCreateRequest, keyword));

    // 하나도 맞는것이 없으면 저장하지 않음
    if(!matchInterest) {
      return null;
    }

    //엔티티
    News news = News.builder()
        .source(newsCreateRequest.source())
        .resourceLink(newsCreateRequest.resourceLink())
        .title(newsCreateRequest.title())
        .postDate(newsCreateRequest.postDate())
        .overview(newsCreateRequest.overView())
        .interest(interest)
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