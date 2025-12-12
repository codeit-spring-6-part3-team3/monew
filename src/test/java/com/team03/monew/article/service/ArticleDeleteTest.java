package com.team03.monew.article.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.dto.ArticleDeleteRequest;
import com.team03.monew.article.exception.ArticleErrorCode;
import com.team03.monew.article.repository.ArticleRepository;
import com.team03.monew.common.customerror.MonewException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleDeleteTest {

  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private BasicArticleService articleService;

  // 뉴스 삭제 실패 확인
  @Test
  @DisplayName("뉴스 삭제 실패 - 뉴스가 없을 때")
  void deleteArticle_NotFound(){
    UUID uuid = UUID.randomUUID();

    when(articleRepository.findById(uuid)).thenReturn(Optional.empty());

    ArticleDeleteRequest articleDeleteRequest = new ArticleDeleteRequest(uuid);

    assertThatThrownBy(()->articleService.deleteArticle_logical(articleDeleteRequest))
        .isInstanceOf(MonewException.class)
        .extracting("errorCode")
        .isEqualTo(ArticleErrorCode.ARTICLE_NOT_FOUND);
  }

  // 뉴스 논리적 삭제 성공
  @Test
  @DisplayName("뉴스 삭제 성공 - 논리")
  void deleteArticle_Success1(){

    // given
    UUID uuid = UUID.randomUUID();

    //뉴스 엔티티 목 객체 생성
    Article article = mock(Article.class);

    //레포에서 findById 호출시 목 반환
    when(articleRepository.findById(uuid)).thenReturn(Optional.of(article));

    ArticleDeleteRequest articleDeleteRequest = new ArticleDeleteRequest(uuid);

    //when
    articleService.deleteArticle_logical(articleDeleteRequest);

    //then
    verify(article).deleteArticle();
  }

  @Test
  @DisplayName("뉴스 삭제 실패(물리) - isDelete가 false 일떄 ")
  void deleteArticle_Fail2(){

    //given
    UUID uuid = UUID.randomUUID();
    Article article = mock(Article.class);

    // 논리 삭제 여부 false일때
    when(article.isDelete()).thenReturn(false);

    when(articleRepository.findById(uuid)).thenReturn(Optional.of(article));

    // when
    assertThatThrownBy(()-> articleService.deleteArticle_physical(new ArticleDeleteRequest(uuid)))
        .isInstanceOf(MonewException.class)
        .extracting("errorCode")
        .isEqualTo(ArticleErrorCode.ARTICLE_CANNOT_DELETE);

    // 레포의 delete가 호출되었는가? 확인
    verify(articleRepository,never()).delete(article);

  }

  @Test
  @DisplayName("뉴스 삭제 성공 - 물리")
  void deleteArticle_Success2(){
    //given
    UUID uuid = UUID.randomUUID();

    Article article = mock(Article.class);

    when(article.isDelete()).thenReturn(true);

    when(articleRepository.findById(uuid)).thenReturn(Optional.of(article));

    //when
    //서비스의 해당 메서드 실행하면
    articleService.deleteArticle_physical(new ArticleDeleteRequest(uuid));

    //then
    //delete가 호출됬는가?
    verify(articleRepository,times(1)).delete(article);

  }
}