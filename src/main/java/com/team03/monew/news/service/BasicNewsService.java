package com.team03.monew.news.service;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.exception.CustomException.SameResourceLink;
import com.team03.monew.news.repository.NewsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicNewsService implements NewsService {

  private final NewsRepository newsRepository;

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
}