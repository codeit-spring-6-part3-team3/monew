package com.team03.monew.article.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team03.monew.article.config.JpaQueryFactoryTestConfig;
import com.team03.monew.article.domain.Article;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleDto;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaQueryFactoryTestConfig.class)
@ActiveProfiles("test")
class ArticleQueryRepositoryTest {

  @Autowired
  EntityManager em;

  @Qualifier("articleQueryRepositoryImpl")
  @Autowired
  ArticleQueryRepository articleQueryRepository;

  @BeforeEach
  void setUp() {
    for (int i = 1; i <= 10; i++) {
      Article article = Article.builder()
          .source(ArticleSourceType.YNA)
          .resourceLink("https://article.com/" + i)
          .title("뉴스 제목 " + i)
          .overview("뉴스 요약 " + i)
          .postedAt(LocalDateTime.now().minusDays(i))
          .commentCount((long) i)
          .viewCount((long) i * 10)
          .isDelete(false)
          .build();

      em.persist(article);
    }
    em.flush();
    em.clear();
  }


  // 뉴스 목록 조회 성공 테스트
  // 디폴트 게시일로 정렬
  @Test
  void findArticleList_Success() {

    // when
    CursorPageResponseArticleDto<ArticleDto> result =
        articleQueryRepository.searchArticles(
            null, null, null,
            null, null,
            "publishDate", "DESC",
            null, null,
            5
        );

    // then
    //페이지 크기 5
    assertThat(result.content()).hasSize(5);
    assertThat(result.hasNext()).isTrue();
  }

  // 읽은 수 기준 내림차순 테스트
  @Test
  void orderByViewCount_Success() {

    //when
    CursorPageResponseArticleDto<ArticleDto> result =
        articleQueryRepository.searchArticles(
            null, null, null,
            null, null,
            "viewCount", "DESC",
            null, null,
            5
        );

    List<ArticleDto> list = result.content();

    //then
    assertThat(list.get(0).viewCount())
        .isGreaterThan(list.get(1).viewCount());
  }

  // 커서 기반 페이지네이션 테스트
  // 10개의 뉴스 중 5개 출력후 다음 5개 확인
  @Test
  void findNextCursor_Success() {

    // given
    // 1번쨰 페이지
    CursorPageResponseArticleDto<ArticleDto> page1 =
        articleQueryRepository.searchArticles(
            null, null, null,
            null, null,
            "publishDate", "DESC",
            null, null,
            5
        );

    //when
    // 2번쨰 페이지 -나머지 뉴스 조회
    CursorPageResponseArticleDto<ArticleDto> page2 =
        articleQueryRepository.searchArticles(
            null, null, null,
            null, null,
            "publishDate", "DESC",
            page1.nextCursor(),
            page1.nextAfter(),
            5
        );

    //then
    // 페이지 크기 검증
    assertThat(page2.content()).hasSize(5);
    assertThat(page2.content().get(0).publishDate())
        .isBefore(page1.content().get(4).publishDate());
  }
  // 뉴스 목록 조회 실패 테스트
  @Test
  void findArticleList_Fail_NoResults() {

    //given
    em.createQuery("UPDATE Article n SET n.isDelete = true").executeUpdate();
    em.flush();
    em.clear();

    //when
    CursorPageResponseArticleDto<ArticleDto> result =
        articleQueryRepository.searchArticles(
            null, null, null,
            null, null,
            "publishDate", "DESC",
            null, null,
            5
        );

    // then
    assertThat(result.content()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursor()).isNull();
    assertThat(result.nextAfter()).isNull();
  }
}
