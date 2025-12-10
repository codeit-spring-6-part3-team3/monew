package com.team03.monew.articleView.service;

import com.team03.monew.articleView.domain.ArticleViews;
import com.team03.monew.articleView.repository.ArticleViewsRepository;
import com.team03.monew.article.domain.Article;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.repository.UserRepository;
import java.util.UUID;

public class BasicArticleViewsService implements ArticleViewsService {

  private ArticleViewsRepository articleViewsRepository;
  private UserRepository userRepository;

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
}
