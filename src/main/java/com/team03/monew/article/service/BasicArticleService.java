package com.team03.monew.article.service;

import com.team03.monew.article.domain.Article;
import com.team03.monew.articleView.service.ArticleViewsService;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleDeleteRequest;
import com.team03.monew.article.dto.ArticleDto;
import com.team03.monew.article.dto.ArticleResponseDto;
import com.team03.monew.article.exception.CustomException.ArticleCanNotDelete;
import com.team03.monew.article.exception.CustomException.ArticleNotFound;
import com.team03.monew.article.exception.CustomException.SameResourceLink;
import com.team03.monew.article.repository.ArticleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Service
public class BasicArticleService implements ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleViewsService articleViewsService;
  private final InterestRepository interestRepository;

  @Override
  public CursorPageResponseArticleDto<ArticleDto> findArticle(
      String keyword,
      UUID interestId,
      List<ArticleSourceType> sourceIn,
      LocalDateTime publishDateFrom,
      LocalDateTime publishDateTo,
      String orderBy,
      String direction, //정렬 asc, desc
      String cursor,
      LocalDateTime after, //보조 커서
      int limit
  ) {
    return articleRepository.searchArticles(
        keyword,
        interestId,
        sourceIn,
        publishDateFrom,
        publishDateTo,
        orderBy,
        direction,
        cursor,
        after,
        limit
    );
  }


  // 뉴스 제목, 요약에 특정 내용이 있는지 확인하는 메서드
  private boolean containText(ArticleCreateRequest articleCreateRequest, String text) {
    return articleCreateRequest.title().contains(text) || articleCreateRequest.overView().contains(text);
  }

  //뉴스 저장(등록)
  @Transactional
  @Override
  public ArticleResponseDto createArticle(ArticleCreateRequest articleCreateRequest, UUID interestId) {

    Optional<Article> existing = articleRepository.findByResourceLink(articleCreateRequest.resourceLink());
    if(existing.isPresent()) {
      throw new SameResourceLink();
    }

    //관심사 조회
    Interest interest = interestRepository.findById(interestId)
        // 관심사 없을떄 예외 발생
        .orElseThrow(() -> new RuntimeException());

    //수집한 기사 중 관심사의 키워드를 포함하는 뉴스 기사만 저장합니다.
    boolean matchInterest = containText(articleCreateRequest, interest.getName()) || interest.getKeywords().stream()
        .anyMatch(keyword -> containText(articleCreateRequest, keyword));

    // 하나도 맞는것이 없으면 저장하지 않음
    if(!matchInterest) {
      return null;
    }

    //엔티티
    Article article = Article.builder()
        .source(articleCreateRequest.source())
        .resourceLink(articleCreateRequest.resourceLink())
        .title(articleCreateRequest.title())
        .postedAt(articleCreateRequest.postDate())
        .overview(articleCreateRequest.overView())
        .interest(interest)
        .build();

    //저장
    Article savedArticle = articleRepository.save(article);

    //dto변환
    return ArticleResponseDto.from(savedArticle);
  }

  // 뉴스 삭제(논리)
  @Transactional
  @Override
  public void deleteArticle_logical(ArticleDeleteRequest articleDeleteRequest) {
    Article article = articleRepository.findById(articleDeleteRequest.articleId())
        .orElseThrow(ArticleNotFound::new);

    article.deleteArticle();
    articleRepository.save(article);
  }

  //뉴스 삭제(물리)
  @Transactional
  @Override
  public void deleteArticle_physical(ArticleDeleteRequest articleDeleteRequest) {
    Article article = articleRepository.findById(articleDeleteRequest.articleId())
        .orElseThrow(ArticleNotFound::new);

    if(!article.isDelete()){
      throw new ArticleCanNotDelete();
    }
    articleRepository.delete(article);
  }

  // 뉴스 단건 조회
  @Override
  public ArticleDto getDetailArticle(UUID articleId, UUID userId) {

    // 뉴스 기사 없을시 에러 처리
    Article article =  articleRepository.findById(articleId)
        .orElseThrow(ArticleNotFound::new);

    //초기 읽은 여부
    boolean viewedByMe = articleViewsService.isRead(article,userId);

    return ArticleDto.from(article,viewedByMe);
  }
}