package com.team03.monew.news.repository;

import static com.team03.monew.articleView.domain.QNewsViews.newsViews;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.domain.QNews;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsQueryRepositoryImpl implements NewsQueryRepository {

  private static final QNews qnews = QNews.news;

  private final JPAQueryFactory queryFactory;

  @Override
  public CursorPageResponseArticleDto<NewsDto> searchNews(

      //해당 키워드는 관심사의 키워드가 아닌 검색어의 키워드입니다 하아...
      String keyword,
      UUID interestId,
      List<NewsSourceType> sourceIn,
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
    List<NewsDto> results = queryFactory
        .select(Projections.constructor(
            NewsDto.class,
            qnews.articleId,
            qnews.source,
            qnews.resourceLink,
            qnews.title,
            qnews.postDate,
            qnews.overview,
            qnews.commentCount,
            qnews.viewCount,
            newsViews.id.isNotNull()
        ))
        .from(qnews)

        // 뉴스 조회 여부 파악을 위해 newsViews와 조인
        .leftJoin(newsViews)

        .on(newsViews.news.articleId.eq(qnews.articleId))
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
            qnews.isDelete.isFalse()
        )
        .orderBy(orderSpecifier)
        .limit(limit + 1)
        .fetch();

    boolean hasNext = results.size() > limit;

    String nextCursor = null;
    LocalDateTime nextAfter = null;

    if (hasNext) {
      NewsDto last = results.remove(limit);
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
            ? qnews.commentCount.goe(subCommentCount(cursorId))
            : qnews.commentCount.loe(subCommentCount(cursorId));

        secondary = qnews.commentCount.eq(subCommentCount(cursorId))
            .and(
                asc ? qnews.postDate.goe(after) : qnews.postDate.loe(after));
      }
      case "viewCount" -> {
        primary = asc
            ? qnews.viewCount.gt(subViewCount(cursorId))
            : qnews.viewCount.lt(subViewCount(cursorId));

        secondary = qnews.viewCount.eq(subViewCount(cursorId))
            .and(
                asc ? qnews.postDate.goe(after) : qnews.postDate.loe(after));
      }
      //정렬 기본 -> 게시일
      default -> {
        return asc
            ? qnews.postDate.goe(after)
            : qnews.postDate.loe(after);
      }
    }
    return primary.or(secondary);
  }

  // 조회 수 서브 쿼리
  private JPQLSubQuery<Long> subViewCount(UUID id) {
    return JPAExpressions
        .select(qnews.viewCount)
        .from(qnews)
        .where(qnews.articleId.eq(id));
  }

  //댓글 수 서브 쿼리
  private JPQLSubQuery<Long> subCommentCount(UUID id) {
    return JPAExpressions
        .select(qnews.commentCount)
        .from(qnews)
        .where(qnews.articleId.eq(id));
  }

  // 정렬 조건 선택
  private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
    boolean asc = "ASC".equalsIgnoreCase(direction);

    return switch (orderBy) {
      case "commentCount" -> asc ? qnews.commentCount.asc() : qnews.commentCount.desc();
      case "viewCount" -> asc ? qnews.viewCount.asc() : qnews.viewCount.desc();
      default -> asc ? qnews.postDate.asc() : qnews.postDate.desc();
    };
  }


  //하나의 검색어로 다음의 속성 중 하나라도 부분일치하는 데이터를 검색할 수 있습니다.
  private BooleanExpression containsSearchTerm(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      return null;
    }
    return qnews.title.contains(searchTerm).or(qnews.overview.contains(searchTerm));
  }

  // 다음의 속성으로 조회할 수 있습니다.
  // 1. 관심사
  private BooleanExpression filterByInterest(UUID interestId) {
    return (interestId == null) ? null : qnews.interest.id.eq(interestId);
  }

  // 2. 출처
  private BooleanExpression filterBySource(List<NewsSourceType> sourceIn) {
    if (sourceIn == null || sourceIn.isEmpty()){
      return null;
    }
    return qnews.source.in(sourceIn);
  }

  // 3. 게시일
  private BooleanExpression filterByDate(LocalDateTime from, LocalDateTime to) {
    if (from == null && to == null) {
      return null;
    }
    if (from == null) {
      return qnews.postDate.loe(to);
    }
    if (to == null){
      return qnews.postDate.goe(from);
    }
    return qnews.postDate.between(from, to);
  }
}
