package com.team03.monew.news.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.news.domain.News;
import com.team03.monew.news.dto.NewsDeleteRequest;
import com.team03.monew.news.exception.CustomException.NewsCanNotDelete;
import com.team03.monew.news.exception.CustomException.NewsNotFound;
import com.team03.monew.news.repository.NewsRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewsDeleteTest {

  @Mock
  private NewsRepository newsRepository;

  @InjectMocks
  private BasicNewsService newsService;

  // 뉴스 삭제 실패 확인
  @Test
  @DisplayName("뉴스 삭제 실패 - 뉴스가 없을 때")
  void deleteNews_NotFound(){
    UUID uuid = UUID.randomUUID();

    when(newsRepository.findById(uuid)).thenReturn(Optional.empty());

    NewsDeleteRequest newsDeleteRequest = new NewsDeleteRequest(uuid);

    assertThatThrownBy(()->newsService.deleteNews_logical(newsDeleteRequest))
        .isInstanceOf(NewsNotFound.class);
  }

  // 뉴스 논리적 삭제 성공
  @Test
  @DisplayName("뉴스 삭제 성공 - 논리")
  void deleteNews_Success1(){

    // given
    UUID uuid = UUID.randomUUID();

    //뉴스 엔티티 목 객체 생성
    News news = mock(News.class);

    //레포에서 findById 호출시 목 반환
    when(newsRepository.findById(uuid)).thenReturn(Optional.of(news));

    NewsDeleteRequest newsDeleteRequest = new NewsDeleteRequest(uuid);

    //when
    newsService.deleteNews_logical(newsDeleteRequest);

    //then
    verify(news).deleteNews();
  }

  @Test
  @DisplayName("뉴스 삭제 실패(물리) - isDelete가 false 일떄 ")
  void deleteNews_Fail2(){

    //given
    UUID uuid = UUID.randomUUID();
    News news = mock(News.class);

    // 논리 삭제 여부 false일때
    when(news.isDelete()).thenReturn(false);

    when(newsRepository.findById(uuid)).thenReturn(Optional.of(news));

    // when
    assertThatThrownBy(()-> newsService.deleteNews_physical(new NewsDeleteRequest(uuid)))
        .isInstanceOf(NewsCanNotDelete.class);

    // 레포의 delete가 호출되었는가? 확인
    verify(newsRepository,never()).delete(news);

  }

  @Test
  @DisplayName("뉴스 삭제 성공 - 물리")
  void deleteNews_Success2(){
    //given
    UUID uuid = UUID.randomUUID();

    News news = mock(News.class);

    when(news.isDelete()).thenReturn(true);

    when(newsRepository.findById(uuid)).thenReturn(Optional.of(news));

    //when
    //서비스의 해당 메서드 실행하면
    newsService.deleteNews_physical(new NewsDeleteRequest(uuid));

    //then
    //delete가 호출됬는가?
    verify(newsRepository,times(1)).delete(news);

  }
}