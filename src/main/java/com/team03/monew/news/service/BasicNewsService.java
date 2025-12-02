package com.team03.monew.news.service;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.exception.CustomException.NewsCanNotDelete;
import com.team03.monew.news.exception.CustomException.NewsNotFound;
import com.team03.monew.news.exception.CustomException.SameResourceLink;
import com.team03.monew.news.repository.NewsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicNewsService implements NewsService {

  private final NewsRepository newsRepository;

  //뉴스 저장(등록)
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
}