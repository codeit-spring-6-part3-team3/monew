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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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

    Article article = articleRepository.findById(articleId)
        .orElseThrow(() -> new MonewException(ArticleErrorCode.ARTICLE_NOT_FOUND));

    User user = userRepository.findById(viewedBy)
        .orElseThrow(() -> new MonewException(UserErrorCode.USER_NOT_FOUND));

    Optional<ArticleViews> existing =
        articleViewsRepository.findByArticleAndUser(article, user);

    ArticleViews articleViews;

    if (existing.isPresent()) {
      articleViews = existing.get();
    } else {

      articleViews = new ArticleViews(user, article);
      articleViewsRepository.save(articleViews);
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
