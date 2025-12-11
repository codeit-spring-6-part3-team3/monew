package com.team03.monew.articleviews.service;

import com.team03.monew.articleviews.domain.ArticleViews;
import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;
import com.team03.monew.articleviews.repository.ArticleViewsRepository;
import com.team03.monew.article.domain.Article;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.repository.UserRepository;

import java.util.List;
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
    return articleViewsRepository.findTopTenByUserIdOrderByCreatedAtDesc(userId);
  }
}
