package com.team03.monew.articleviews.service;

import com.team03.monew.article.exception.ArticleErrorCode;
import com.team03.monew.article.repository.ArticleRepository;
import com.team03.monew.articleviews.domain.ArticleViews;
import com.team03.monew.articleviews.dto.ArticleViewDto;
import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;
import com.team03.monew.articleviews.repository.ArticleViewsRepository;
import com.team03.monew.article.domain.Article;
import com.team03.monew.common.customerror.MonewException;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.exception.UserErrorCode;
import com.team03.monew.user.exception.UserNotFoundException;
import com.team03.monew.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicArticleViewsService implements ArticleViewsService {

  private final ArticleViewsRepository articleViewsRepository;
  private final UserRepository userRepository;
  private final ArticleRepository articleRepository;

  @Override
  public boolean isRead(Article article, UUID userId) {
    if(userId == null){
      return false;
    }

    boolean alreadyRead = articleViewsRepository.existsByArticleIdAndUserId(article.getId(), userId);
    //이미 뉴스를 읽었는지 확인
    if(alreadyRead){
      return true;
    }

    User user = userRepository.getReferenceById(userId);
    articleViewsRepository.save(new ArticleViews(user,article));
    article.increaseViewCount();

    return true;
  }

  @Override
  @Transactional
  public List<ArticleViewsActivityDto> topTenByUserId(UUID userId) {

      User user = userRepository.getReferenceById(userId);
      if(user.getId() == null){
          throw new UserNotFoundException();
      }
      List<ArticleViews> articleViewsList = articleViewsRepository.findTop10ByUserOrderByCreatedAtDesc(user);

      return toDtoList(articleViewsList);

  }

  @Override
  @Transactional
  public ArticleViewDto registerArticleViews(UUID articleId, UUID viewedBy) {

    log.info(" 기사 뷰 등록  요청. articleId={}, viewedBy={}", articleId, viewedBy);

    // 기사 존재 확인
    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> {
          log.warn(" 기사 없음. articleId={}", articleId);
          return new MonewException(ArticleErrorCode.ARTICLE_NOT_FOUND);
        });
    log.debug("기사 조회 완료. title={}, source={}",
        article.getTitle(), article.getSource());

    // 사용자 존재 확인
    User user = userRepository.findById(viewedBy)
        .orElseThrow(() -> {
          log.warn("사용자 없음. viewedBy={}", viewedBy);
          return new MonewException(UserErrorCode.USER_NOT_FOUND);
        });
    log.debug("사용자 조회 완료. userId={}", user.getId());

    // 이전에 해당 기사 조회 했냐
    Optional<ArticleViews> existing =
        articleViewsRepository.findByArticleAndUser(article, user);

    ArticleViews articleViews;

    if (existing.isPresent()) {

      articleViews = existing.get();
      log.info("기존에 조회 완료. articleViewId={}", articleViews.getId());

    }
    else {

      articleViews = new ArticleViews(user, article);
      articleViewsRepository.save(articleViews);

      log.info("기사 뷰 등록 완료. articleViewId={}, articleId={}, viewedBy={}",
          articleViews.getId(), articleId, viewedBy);
    }

    return new ArticleViewDto(
        articleViews.getId(),
        user.getId(),
        articleViews.getCreatedAt(),
        article.getId(),
        article.getSource().name(),
        article.getResourceLink(),
        article.getTitle(),
        article.getPostedAt(),
        article.getOverview(),
        article.getCommentCount(),
        article.getViewCount()
    );
  }


  private List<ArticleViewsActivityDto> toDtoList(List<ArticleViews> articleViewsList) {
      List<ArticleViewsActivityDto> articleViewsActivityDtoList = new ArrayList<>();

      for (ArticleViews articleViews : articleViewsList) {
          ArticleViewsActivityDto articleViewsActivityDto = new ArticleViewsActivityDto(articleViews);
          articleViewsActivityDtoList.add(articleViewsActivityDto);
      }
      return articleViewsActivityDtoList;
  }
}
