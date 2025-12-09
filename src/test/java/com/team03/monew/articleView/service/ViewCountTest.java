package com.team03.monew.articleView.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team03.monew.articleView.domain.NewsViews;
import com.team03.monew.articleView.repository.NewsViewsRepository;
import com.team03.monew.news.domain.News;
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
  private NewsViewsRepository newsViewsRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private BasicNewsViewsService newsViewsService;

  private News news;
  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    news = mock(News.class);
    userId = UUID.randomUUID();
    user = mock(User.class);

    when(news.getArticleId()).thenReturn(UUID.randomUUID());
  }

  // 사용자 id가 널일때
  @Test
  void userIdIsNullTest() {

    // given
    UUID nullUserId = null;

    // when
    // 널 유저가 읽었는가?
    boolean result = newsViewsService.isRead(news, nullUserId);

    //then
    // 결과가 falser가 나와야 테스트 통과
    assertThat(result).isFalse();
  }

  // 사용자가 이미 읽었을때
  @Test
  void isRead_alreadyReadTest() {
    //given
    // 사용자랑 뉴스기사가 연관된적이 있으면
    when(newsViewsRepository.existsByArticleIdAndUserId(news.getArticleId(), userId))
        .thenReturn(true);

    //when
    // isRead 값이 주어질떄
    boolean result = newsViewsService.isRead(news, userId);

    //then
    // result가 true인지 확인 ->맞기에 여기서 패스
    assertThat(result).isTrue();
    // 호출되었는지 검증 -> 여기서도 호출되었으니까 확인
    verify(newsViewsRepository).existsByArticleIdAndUserId(news.getArticleId(), userId);

    // 읽은 수 증가 메서드가 호출되지 않았음 확인
    verify(news, never()).increaseViewCount();

  }

  // 처음 읽을 때
  @Test
  void isRead_FirstTime(){
    // given
    //한번도 안읽었기에 existsByArticleIdAndUserId 호출된적없으니 false를 제공
    when(newsViewsRepository.existsByArticleIdAndUserId(news.getArticleId(), userId))
        .thenReturn(false);
    when(userRepository.getReferenceById(userId)).thenReturn(user);

    // when
    boolean result = newsViewsService.isRead(news, userId);

    // then
    assertThat(result).isTrue();
    // existsByArticleIdAndUserId 얘 호출되었는지 확인 -> 호출되어서 패스
    verify(newsViewsRepository).existsByArticleIdAndUserId(news.getArticleId(), userId);
    // 사용자 아이디도 호출 되었으니 패스
    verify(userRepository).getReferenceById(userId);
    verify(newsViewsRepository).save(any(NewsViews.class));
    // 읽은수 증가 로직 호출되었는지 확인
    verify(news).increaseViewCount();
  }
}
