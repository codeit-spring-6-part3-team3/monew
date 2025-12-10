package com.team03.monew.articleView.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.articleView.domain.ArticleViews;
import com.team03.monew.articleView.repository.ArticleViewsRepository;
import com.team03.monew.article.domain.Article;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ViewCountTest {

  @Mock
  private ArticleViewsRepository articleViewsRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BasicArticleViewsService articleViewsService;

  private Article article;
  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    article = mock(Article.class);
    userId = UUID.randomUUID();
    user = mock(User.class);

    when(article.getId()).thenReturn(UUID.randomUUID());
  }

  // 사용자 id가 널일때
  @Test
  void userIdIsNullTest() {

    // given
    UUID nullUserId = null;

    // when
    // 널 유저가 읽었는가?
    boolean result = articleViewsService.isRead(article, nullUserId);

    //then
    // 결과가 falser가 나와야 테스트 통과
    assertThat(result).isFalse();
  }

  // 사용자가 이미 읽었을때
  @Test
  void isRead_alreadyReadTest() {
    //given
    // 사용자랑 뉴스기사가 연관된적이 있으면
    when(articleViewsRepository.existsByArticleIdAndUserId(article.getId(), userId))
        .thenReturn(true);

    //when
    // isRead 값이 주어질떄
    boolean result = articleViewsService.isRead(article, userId);

    //then
    // result가 true인지 확인 ->맞기에 여기서 패스
    assertThat(result).isTrue();
    // 호출되었는지 검증 -> 여기서도 호출되었으니까 확인
    verify(articleViewsRepository).existsByArticleIdAndUserId(article.getId(), userId);

    // 읽은 수 증가 메서드가 호출되지 않았음 확인
    verify(article, never()).increaseViewCount();

  }

  // 처음 읽을 때
  @Test
  void isRead_FirstTime(){
    // given
    //한번도 안읽었기에 existsByArticleIdAndUserId 호출된적없으니 false를 제공
    when(articleViewsRepository.existsByArticleIdAndUserId(article.getId(), userId))
        .thenReturn(false);
    when(userRepository.getReferenceById(userId)).thenReturn(user);

    // when
    boolean result = articleViewsService.isRead(article, userId);

    // then
    assertThat(result).isTrue();
    // existsByArticleIdAndUserId 얘 호출되었는지 확인 -> 호출되어서 패스
    verify(articleViewsRepository).existsByArticleIdAndUserId(article.getId(), userId);
    // 사용자 아이디도 호출 되었으니 패스
    verify(userRepository).getReferenceById(userId);
    verify(articleViewsRepository).save(any(ArticleViews.class));
    // 읽은수 증가 로직 호출되었는지 확인
    verify(article).increaseViewCount();
  }
}
