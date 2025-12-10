package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.news.collect.domain.FetchedNews;
import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KeywordFilterServiceTest {

  @Mock
  InterestRepository interestRepository;

  @InjectMocks
  KeywordFilterService keywordFilterService;

  @Test
  @DisplayName("refresh: 키워드 인덱스를 구축하고 제목/본문에 키워드가 포함된 관심사를 반환한다")
  void refresh_and_match() {
    Interest ai = Interest.builder().name("AI").keywords(List.of("AI", "인공지능")).build();
    Interest economy = Interest.builder().name("경제").keywords(List.of("경제")).build();

    // ID 설정
    ReflectionTestUtils.setField(ai, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(economy, "id", UUID.randomUUID());

    given(interestRepository.findAll()).willReturn(List.of(ai, economy));

    keywordFilterService.refresh(); // refresh 호출

    FetchedNews news = new FetchedNews(
        NewsSourceType.CHOSUN,
        "url",
        "인공지능이 경제를 바꾼다",
        LocalDateTime.now(),
        "요약"
    );

    Set<Interest> result = keywordFilterService.matchingInterests(news);

    assertThat(result)
        .containsExactlyInAnyOrder(ai, economy);
  }

  @Test
  @DisplayName("matchingInterests: 등록된 키워드가 없거나 뉴스가 null이면 빈 Set을 반환한다")
  void matchingInterests_emptyWhenNoIndexOrNews() {
    // index 비워둔 상태
    Set<Interest> resultNullNews = keywordFilterService.matchingInterests(null);
    assertThat(resultNullNews).isEmpty();
  }
}
