package com.team03.monew.article.repository;

import static com.team03.monew.articleviews.domain.QArticleViews.articleViews;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.domain.QArticle;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository {

  private static final QArticle qarticle = QArticle.article;

  private final JPAQueryFactory queryFactory;

  @Override
  public CursorPageResponseArticleDto<ArticleDto> searchArticles(

      //해당 키워드는 관심사의 키워드가 아닌 검색어의 키워드입니다 하아...
      String keyword,
      UUID interestId,
      List<ArticleSourceType> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {

    //정렬 조건 생성
    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderBy, direction);

    // 뉴스 목록 조회 쿼리
    List<ArticleDto> results = queryFactory
        .select(Projections.constructor(
            ArticleDto.class,
            qarticle.id,
            qarticle.source,
            qarticle.resourceLink,
            qarticle.title,
            qarticle.postedAt,
            qarticle.overview,
            qarticle.commentCount,
            qarticle.viewCount,
            articleViews.id.isNotNull()
        ))

        .from(qarticle)

        // 뉴스 조회 여부 파악을 위해 articleViews와 조인
        .leftJoin(articleViews)

        .on(articleViews.article.id.eq(qarticle.id))
        .where(
            // 검색어
            containsSearchTerm(keyword),
            //관심사
            filterByInterest(interestId),
            // 출처
            filterBySource(sourceIn),
            // 날짜
            filterByDate(publishDateFrom, publishDateTo),
            //커서 조건
            cursorCondition(cursor, after, orderBy, direction),
            qarticle.isDelete.isFalse()
        )
        .orderBy(orderSpecifier)
        .limit(limit + 1)
        .fetch();

    boolean hasNext = results.size() > limit;

    String nextCursor = null;
    LocalDateTime nextAfter = null;

    if (hasNext) {
      ArticleDto last = results.remove(limit);
      nextCursor = last.id().toString();
      nextAfter = last.publishDate();
    }

    return CursorPageResponseArticleDto.of(
        results,
        nextCursor,
        nextAfter,
        limit,
        null,
        hasNext
    );
  }

  // 커서 조건
  private BooleanExpression cursorCondition(
      String cursor,
      LocalDateTime after,
      String orderBy,
      String direction
  ) {
    if (cursor == null || after == null) return null;

    UUID cursorId = UUID.fromString(cursor);
    boolean asc = "ASC".equalsIgnoreCase(direction);

    BooleanExpression primary;
    BooleanExpression secondary;

    switch (orderBy) {
      case "commentCount" -> {
        primary = asc
            ? qarticle.commentCount.goe(subCommentCount(cursorId))
            : qarticle.commentCount.loe(subCommentCount(cursorId));

        secondary = qarticle.commentCount.eq(subCommentCount(cursorId))
            .and(
                asc ? qarticle.postedAt.goe(after) : qarticle.postedAt.loe(after));
      }
      case "viewCount" -> {
        primary = asc
            ? qarticle.viewCount.gt(subViewCount(cursorId))
            : qarticle.viewCount.lt(subViewCount(cursorId));

        secondary = qarticle.viewCount.eq(subViewCount(cursorId))
            .and(
                asc ? qarticle.postedAt.goe(after) : qarticle.postedAt.loe(after));
      }
      //정렬 기본 -> 게시일
      default -> {
        return asc
            ? qarticle.postedAt.goe(after)
            : qarticle.postedAt.loe(after);
      }
    }
    return primary.or(secondary);
  }

  // 조회 수 서브 쿼리
  private JPQLSubQuery<Long> subViewCount(UUID id) {
    return JPAExpressions
        .select(qarticle.viewCount)
        .from(qarticle)
        .where(qarticle.id.eq(id));
  }

  //댓글 수 서브 쿼리
  private JPQLSubQuery<Long> subCommentCount(UUID id) {
    return JPAExpressions
        .select(qarticle.commentCount)
        .from(qarticle)
        .where(qarticle.id.eq(id));
  }

  // 정렬 조건 선택
  private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
    boolean asc = "ASC".equalsIgnoreCase(direction);

    return switch (orderBy) {
      case "commentCount" -> asc ? qarticle.commentCount.asc() : qarticle.commentCount.desc();
      case "viewCount" -> asc ? qarticle.viewCount.asc() : qarticle.viewCount.desc();
      default -> asc ? qarticle.postedAt.asc() : qarticle.postedAt.desc();
    };
  }


  //하나의 검색어로 다음의 속성 중 하나라도 부분일치하는 데이터를 검색할 수 있습니다.
  private BooleanExpression containsSearchTerm(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return null;
    }
    return qarticle.title.contains(searchTerm).or(qarticle.overview.contains(searchTerm));
  }

  // 다음의 속성으로 조회할 수 있습니다.
  // 1. 관심사
  private BooleanExpression filterByInterest(UUID interestId) {
    return (interestId == null) ? null : qarticle.interest.id.eq(interestId);
  }

  // 2. 출처
  private BooleanExpression filterBySource(List<ArticleSourceType> sourceIn) {
    if (sourceIn == null || sourceIn.isEmpty()){
      return null;
    }
    return qarticle.source.in(sourceIn);
  }

  // 3. 게시일
  private BooleanExpression filterByDate(LocalDateTime from, LocalDateTime to) {
    if (from == null && to == null) {
      return null;
    }
    if (from == null) {
      return qarticle.postedAt.loe(to);
    }
    if (to == null){
      return qarticle.postedAt.goe(from);
    }
    return qarticle.postedAt.between(from, to);
  }
}
