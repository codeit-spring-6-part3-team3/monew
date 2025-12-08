package com.team03.monew.news.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.news.domain.News;
import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.NewsCreateRequest;
import com.team03.monew.news.dto.NewsResponseDto;
import com.team03.monew.news.exception.CustomException.SameResourceLink;
import com.team03.monew.news.fixture.NewsFixture;
import com.team03.monew.news.repository.NewsRepository;
import java.time.LocalDateTime;
import java.util.List;
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

  @Mock
  private InterestRepository interestRepository;

  @InjectMocks
  private BasicNewsService basicNewsService;

  @Test
  @DisplayName("뉴스 저장 실패 - 중복 뉴스 링크")
  void createNews_Fail_sameResourceLink(){

    //given
    UUID interestId = UUID.randomUUID();
    NewsCreateRequest newsCreateRequest = new NewsCreateRequest(
        NewsSourceType.CHOSUN,
        "https://test.com",
        "테스트 제목",
        LocalDateTime.now(),
        "테스트 요약"
    );
    News news = NewsFixture.newsCreate(newsCreateRequest);


    when(newsRepository.findByResourceLink(newsCreateRequest.resourceLink()))
        .thenReturn(Optional.of(news));

    //when then
      assertThatThrownBy(()-> basicNewsService.createNews(newsCreateRequest, interestId))
        .isInstanceOf(SameResourceLink.class);

    verify(newsRepository, times(1)).findByResourceLink(anyString());
    verify(newsRepository, never()).save(any());

  }

  //관심사 매칭 안될때
  @Test
  @DisplayName("뉴스 저장 실패 - 관심사 일치하지 않음")
  void createNews_Fail_NotMatchInterest(){

    //given
    UUID interestId = UUID.randomUUID();
    NewsCreateRequest newsCreateRequest = new NewsCreateRequest(
        NewsSourceType.CHOSUN,
        "https://test.com",
        "뉴스 제목",
        LocalDateTime.now(),
        "뉴스 요약"
    );

    // 관심사 이름, 키워드와 매칭 안됌 -> 관심사 이름, 키워드가 기존의 title, overview와 다름
    Interest interest = new Interest();
    ReflectionTestUtils.setField(interest, "name", "다른 관심사");
    ReflectionTestUtils.setField(interest, "keywords", List.of("키워드1", "키워드2"));

    // 중복 링크 없다
    when(newsRepository.findByResourceLink(newsCreateRequest.resourceLink()))
        .thenReturn(Optional.empty());
    // 관심사 엔티티에 관심사 있다
    when(interestRepository.findById(interestId))
        .thenReturn(Optional.of(interest));

    // when
    // createNews 호출
    NewsResponseDto result = basicNewsService.createNews(newsCreateRequest, interestId);

    //then
    // 관심사 일치하지 않으니 null 반환
    assertThat(result).isNull();

    // 중복 링크 검사는 한번만
    verify(newsRepository, times(1)).findByResourceLink(anyString());
    // 저장되지 않는것 확인
    verify(newsRepository, never()).save(any());
  }

  // 저장 성공
  @Test
  @DisplayName("뉴스 저장 성공")
  void createNews_Success() {

    //given
    UUID id = UUID.randomUUID();
    UUID interestId = UUID.randomUUID();

    NewsCreateRequest newsCreateRequest = new NewsCreateRequest(
        NewsSourceType.CHOSUN,
        "https://test.com",
        "테스트 제목",
        LocalDateTime.now(),
        "테스트 요약"
    );

    // 관심사 이름 또는 키워드가 뉴스 내용과 매칭
    Interest interest = new Interest();
    ReflectionTestUtils.setField(interest, "name", "테스트");
    ReflectionTestUtils.setField(interest, "keywords", List.of("키워드1", "키워드2"));

    // 중복 링크 없다 given
    when(newsRepository.findByResourceLink(newsCreateRequest.resourceLink()))
        .thenReturn(Optional.empty());
    // 관심사 아이디 있다고 given
    when(interestRepository.findById(interestId))
        .thenReturn(Optional.of(interest));


    when(newsRepository.save(any(News.class))).thenAnswer(invocation -> {
      News news = invocation.getArgument(0);
      ReflectionTestUtils.setField(news, "articleId", id);
      return news;
    });

    //when
    // 저장 메서드 호출
    NewsResponseDto newsResponseDto = basicNewsService.createNews(newsCreateRequest,interestId);

    //then
    //newRepo의 svae()가 한번호출되었는지 확인
    verify(newsRepository, times(1)).save(any(News.class));

    // DTO 올바르게 생성되었는지 검증
    assertThat(newsResponseDto.id()).isEqualTo(id);
    assertThat(newsResponseDto.source()).isEqualTo(newsCreateRequest.source());
    assertThat(newsResponseDto.resourceLink()).isEqualTo(newsCreateRequest.resourceLink());
    assertThat(newsResponseDto.title()).isEqualTo(newsCreateRequest.title());
    assertThat(newsResponseDto.postDate()).isEqualTo(newsCreateRequest.postDate());
    assertThat(newsResponseDto.overView()).isEqualTo(newsCreateRequest.overView());

  }

}