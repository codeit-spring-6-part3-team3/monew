package com.team03.monew.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.article.exception.ArticleErrorCode;
import com.team03.monew.common.customerror.MonewException;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.article.domain.Article;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleResponseDto;
import com.team03.monew.article.fixture.ArticleFixture;
import com.team03.monew.article.repository.ArticleRepository;
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
public class ArticleCreateTest {


  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private InterestRepository interestRepository;

  @InjectMocks
  private BasicArticleService basicArticleService;

  @Test
  @DisplayName("뉴스 저장 실패 - 중복 뉴스 링크")
  void createArticle_Fail_sameResourceLink(){

    //given
    UUID interestId = UUID.randomUUID();
    ArticleCreateRequest articleCreateRequest = new ArticleCreateRequest(
        ArticleSourceType.CHOSUN,
        "https://test.com",
        "테스트 제목",
        LocalDateTime.now(),
        "테스트 요약"
    );
    Article article = ArticleFixture.articleCreate(articleCreateRequest);


    when(articleRepository.findByResourceLink(articleCreateRequest.resourceLink()))
        .thenReturn(Optional.of(article));

    //when then
      assertThatThrownBy(()-> basicArticleService.createArticle(articleCreateRequest, interestId))
        .isInstanceOf(MonewException.class)
          .extracting("errorCode")
          .isEqualTo(ArticleErrorCode.ARTICLE_DUPLICATE_LINK);

    verify(articleRepository, times(1)).findByResourceLink(anyString());
    verify(articleRepository, never()).save(any());

  }

  //관심사 매칭 안될때
  @Test
  @DisplayName("뉴스 저장 실패 - 관심사 일치하지 않음")
  void createArticle_Fail_NotMatchInterest(){

    //given
    UUID interestId = UUID.randomUUID();
    ArticleCreateRequest articleCreateRequest = new ArticleCreateRequest(
        ArticleSourceType.CHOSUN,
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
    when(articleRepository.findByResourceLink(articleCreateRequest.resourceLink()))
        .thenReturn(Optional.empty());
    // 관심사 엔티티에 관심사 있다
    when(interestRepository.findById(interestId))
        .thenReturn(Optional.of(interest));

    // when
    // createArticle 호출
    ArticleResponseDto result = basicArticleService.createArticle(articleCreateRequest, interestId);

    //then
    // 관심사 일치하지 않으니 null 반환
    assertThat(result).isNull();

    // 중복 링크 검사는 한번만
    verify(articleRepository, times(1)).findByResourceLink(anyString());
    // 저장되지 않는것 확인
    verify(articleRepository, never()).save(any());
  }

  // 저장 성공
  @Test
  @DisplayName("뉴스 저장 성공")
  void createArticle_Success() {

    //given
    UUID id = UUID.randomUUID();
    UUID interestId = UUID.randomUUID();

    ArticleCreateRequest articleCreateRequest = new ArticleCreateRequest(
        ArticleSourceType.CHOSUN,
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
    when(articleRepository.findByResourceLink(articleCreateRequest.resourceLink()))
        .thenReturn(Optional.empty());
    // 관심사 아이디 있다고 given
    when(interestRepository.findById(interestId))
        .thenReturn(Optional.of(interest));


    when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
      Article article = invocation.getArgument(0);
      ReflectionTestUtils.setField(article, "id", id);
      return article;
    });

    //when
    // 저장 메서드 호출
    ArticleResponseDto articleResponseDto = basicArticleService.createArticle(articleCreateRequest,interestId);

    //then
    //newRepo의 svae()가 한번호출되었는지 확인
    verify(articleRepository, times(1)).save(any(Article.class));

    // DTO 올바르게 생성되었는지 검증
    assertThat(articleResponseDto.id()).isEqualTo(id);
    assertThat(articleResponseDto.source()).isEqualTo(articleCreateRequest.source());
    assertThat(articleResponseDto.resourceLink()).isEqualTo(articleCreateRequest.resourceLink());
    assertThat(articleResponseDto.title()).isEqualTo(articleCreateRequest.title());
    assertThat(articleResponseDto.postDate()).isEqualTo(articleCreateRequest.postDate());
    assertThat(articleResponseDto.overView()).isEqualTo(articleCreateRequest.overView());

  }
}