package com.team03.monew.article.service;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.exception.ArticleErrorCode;
import com.team03.monew.articleviews.service.ArticleViewsService;
import com.team03.monew.common.customerror.MonewException;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.exception.InterestErrorCode;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.article.domain.ArticleSourceType;
import com.team03.monew.article.dto.CursorPageResponseArticleDto;
import com.team03.monew.article.dto.ArticleCreateRequest;
import com.team03.monew.article.dto.ArticleDeleteRequest;
import com.team03.monew.article.dto.ArticleDto;
import com.team03.monew.article.dto.ArticleResponseDto;
import com.team03.monew.article.repository.ArticleRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    log.debug("뉴스 목록 발견. , 제목 : {}, 관심사 : {}, 출처 : {} ", keyword, interestId, sourceIn );

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

    log.info("뉴스 기사 등록 시도. title : {}, source : {}, resourceLink :{}, interestId : {}"
        ,articleCreateRequest.title(), articleCreateRequest.source(), articleCreateRequest.resourceLink(), interestId);

    Optional<Article> existing = articleRepository.findByResourceLink(articleCreateRequest.resourceLink());
    if(existing.isPresent()) {
      log.warn("중복된 뉴스 링크 resourceLink : {}", articleCreateRequest.resourceLink());
      throw new MonewException(ArticleErrorCode.ARTICLE_DUPLICATE_LINK);
    }

    //관심사 조회
    Interest interest = interestRepository.findById(interestId)
        // 관심사 없을떄 예외 발생
        .orElseThrow(() ->{
          log.warn("관심사 없음. interestId : {}", interestId);
          return new MonewException(InterestErrorCode.INTERESTS_NOT_FOUND);
        });


    //수집한 기사 중 관심사의 키워드를 포함하는 뉴스 기사만 저장합니다.
    boolean matchInterest = containText(articleCreateRequest, interest.getName()) || interest.getKeywords().stream()
        .anyMatch(keyword -> containText(articleCreateRequest, keyword));

    // 하나도 맞는것이 없으면 저장하지 않음
    if(!matchInterest) {
      log.info("관심사, 키워드와 맞지 않는 뉴스 기사. title : {}, overview : {}, interestId: {}"
          ,articleCreateRequest.title(), articleCreateRequest.overView(), interestId);
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

    log.info("뉴스 등록 성공. articleId: {}", savedArticle.getId());

    //dto변환
    return ArticleResponseDto.from(savedArticle);
  }

  // 뉴스 삭제(논리)
  @Transactional
  @Override
  public void deleteArticle_logical(ArticleDeleteRequest articleDeleteRequest) {

    log.info("뉴스 논리 삭제 시도. articleId : {}",articleDeleteRequest.articleId());

    Article article = articleRepository.findById(articleDeleteRequest.articleId())
        .orElseThrow ( () -> {
          log.warn("뉴스 발견되지 않음 articleId : {}", articleDeleteRequest.articleId());
          return new MonewException(ArticleErrorCode.ARTICLE_NOT_FOUND);
        });

    article.deleteArticle();
    articleRepository.save(article);
    log.info("뉴스 논리 삭제 성공. articleId: {}", articleDeleteRequest.articleId());
  }

  //뉴스 삭제(물리)
  @Transactional
  @Override
  public void deleteArticle_physical(ArticleDeleteRequest articleDeleteRequest) {

    log.info("뉴스 물리 삭제 시작. articleId : {}",articleDeleteRequest.articleId());

    Article article = articleRepository.findById(articleDeleteRequest.articleId())
        .orElseThrow ( () ->{
            log.warn("뉴스 발견되지 않음. articleId : {}", articleDeleteRequest.articleId());
            return new MonewException(ArticleErrorCode.ARTICLE_NOT_FOUND);
        });

    if(!article.isDelete()){
      log.warn("뉴스 물리 삭제 거부. articleId : {}, isDelete : {}", articleDeleteRequest.articleId(),article.isDelete());
      throw new MonewException(ArticleErrorCode.ARTICLE_CANNOT_DELETE);
    }

    articleRepository.delete(article);
    log.info("물리 삭제 성공. articleId: {}", articleDeleteRequest.articleId());

  }

  // 뉴스 단건 조회

  @Override
  @Transactional // 내부에서 조회수 증가하기에 read only는 안씀
  public ArticleDto getDetailArticle(UUID articleId, UUID userId) {

    log.debug("뉴스 상세 조회 시작. articleId : {}, userId : {}", articleId, userId);

    // 뉴스 기사 없을시 에러 처리
    Article article =  articleRepository.findById(articleId)
        .orElseThrow ( () ->{
          log.warn("뉴스 조회 되지 않음. articleId : {}", articleId);
           return new MonewException(ArticleErrorCode.ARTICLE_NOT_FOUND);
        });

    //초기 읽은 여부
    boolean viewedByMe = articleViewsService.isRead(article,userId);

    log.debug("뉴스 상세 조회 성공. articleId : {}, viewedByMe : {}", articleId, viewedByMe);
    return ArticleDto.from(article,viewedByMe);
  }

  @Override
  public ArticleSourceType[] getAllSources() {
    return ArticleSourceType.values();
  }
}