package com.team03.monew.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.exception.CustomException.SameResourceLink;
import com.team03.monew.news.fixture.NewsFixture;
import com.team03.monew.news.repository.NewsRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class NewsCreateTest {


  @Mock
  private NewsRepository newsRepository;

  @InjectMocks
  private BasicNewsService basicNewsService;

  @Test
  @DisplayName("뉴스 저장 실패 - 중복 뉴스 링크")
  void createNews_Fail_sameResourceLink(){

    //given

    NewsCreateRequest newsCreateRequest = new NewsCreateRequest(
        NewsSourceType.chosun,
        "https://test.com",
        "테스트 제목",
        LocalDateTime.now(),
        "테스트 요약"
    );
    News news = NewsFixture.newsCreate(newsCreateRequest);


    when(newsRepository.findByResourceLink(newsCreateRequest.resourceLink()))
        .thenReturn(Optional.of(news));

    //when then
    assertThatThrownBy(()-> basicNewsService.createNews(newsCreateRequest))
        .isInstanceOf(SameResourceLink.class);

    verify(newsRepository, times(1)).findByResourceLink(anyString());
    verify(newsRepository, never()).save(any());

  }

  // 저장 성공
  @Test
  @DisplayName("뉴스 저장 성공")
  void createNews_Success() {
    //given
    NewsCreateRequest newsCreateRequest = new NewsCreateRequest(
        NewsSourceType.chosun,
        "https://test.com",
        "테스트 제목",
        LocalDateTime.now(),
        "테스트 요약"
    );

    UUID id = UUID.randomUUID();

    when(newsRepository.save(any(News.class))).thenAnswer(invocation -> {
      News news = invocation.getArgument(0);
      ReflectionTestUtils.setField(news, "articleId", id);
      return news;
    });

    //when
    NewsResponseDto newsResponseDto = basicNewsService.createNews(newsCreateRequest);

    //then
    verify(newsRepository, times(1)).save(any(News.class));
    assertThat(newsResponseDto.id()).isEqualTo(id);
    assertThat(newsResponseDto.title()).isEqualTo(newsCreateRequest.title());
  }

}