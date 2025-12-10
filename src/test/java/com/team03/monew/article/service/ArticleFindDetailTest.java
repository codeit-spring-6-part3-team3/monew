package com.team03.monew.article.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.article.domain.Article;
import com.team03.monew.articleviews.service.ArticleViewsService;
import com.team03.monew.article.dto.ArticleDto;
import com.team03.monew.article.exception.CustomException.ArticleNotFound;
import com.team03.monew.article.repository.ArticleRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleFindDetailTest {

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private ArticleViewsService articleViewsService;

  @InjectMocks
  private BasicArticleService basicArticleService;

  private UUID articleId;
  private UUID userId;
  private com.team03.monew.article.domain.Article article;

  @BeforeEach
  void setUp() {
    articleId = UUID.randomUUID();
    userId = UUID.randomUUID();

    // 가짜 객체 주입
    article = mock(Article.class);
    when(article.getId()).thenReturn(articleId);
  }

  //뉴스 단편 조회 실패
  @Test
  void getDetailArticle_Fail() {
    // 존재하지 않는 뉴스 조회시 예외처리 발생하는지 확인
    //given
    when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicArticleService.getDetailArticle(articleId, userId))
        .isInstanceOf(ArticleNotFound.class);

    // 호출되었는지 확인
    verify(articleRepository).findById(articleId);
  }

  // 뉴스 단편 조회 성공
  @Test
  void getDetailArticle_Success() {
    // 존재하는 뉴스 조회했을 떄 정상적으로 dto반환하는지 확인
    // given
    when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
    // 유저가 이미 읽었을때
    when(articleViewsService.isRead(article, userId)).thenReturn(true);

    // when
    // 뉴스 조회 및 읽음 처리
    ArticleDto result = basicArticleService.getDetailArticle(articleId, userId);

    // then
    assertThat(result.id()).isEqualTo(articleId);
    // viewedByMe ture인지 검증
    assertThat(result.viewedByMe()).isTrue();

    // isRead호출 확인
    verify(articleViewsService).isRead(article, userId);
  }


}

