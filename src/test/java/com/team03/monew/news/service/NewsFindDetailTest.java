package com.team03.monew.news.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.team03.monew.articleView.service.NewsViewsService;
import com.team03.monew.news.domain.News;
import com.team03.monew.news.dto.NewsDto;
import com.team03.monew.news.exception.CustomException.NewsNotFound;
import com.team03.monew.news.repository.NewsRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewsFindDetailTest {

  @Mock
  private NewsRepository newsRepository;

  @Mock
  private NewsViewsService newsViewsService;

  @InjectMocks
  private BasicNewsService basicNewsService;

  private UUID articleId;
  private UUID userId;
  private News news;

  @BeforeEach
  void setUp() {
    articleId = UUID.randomUUID();
    userId = UUID.randomUUID();

    // 가짜 객체 주입
    news = mock(News.class);
    when(news.getArticleId()).thenReturn(articleId);
  }

  //뉴스 단편 조회 실패
  @Test
  void getDetailNews_Fail() {
    // 존재하지 않는 뉴스 조회시 예외처리 발생하는지 확인
    //given
    when(newsRepository.findById(articleId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicNewsService.getDetailNews(articleId, userId))
        .isInstanceOf(NewsNotFound.class);

    // 호출되었는지 확인
    verify(newsRepository).findById(articleId);
  }

  // 뉴스 단편 조회 성공
  @Test
  void getDetailNews_Success() {
    // 존재하는 뉴스 조회했을 떄 정상적으로 dto반환하는지 확인
    // given
    when(newsRepository.findById(articleId)).thenReturn(Optional.of(news));
    // 유저가 이미 읽었을때
    when(newsViewsService.isRead(news, userId)).thenReturn(true);

    // when
    // 뉴스 조회 및 읽음 처리
    NewsDto result = basicNewsService.getDetailNews(articleId, userId);

    // then
    assertThat(result.id()).isEqualTo(articleId);
    // viewedByMe ture인지 검증
    assertThat(result.viewedByMe()).isTrue();

    // isRead호출 확인
    verify(newsViewsService).isRead(news, userId);
  }


}

