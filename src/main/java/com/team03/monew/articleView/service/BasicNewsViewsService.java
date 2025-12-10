package com.team03.monew.articleView.service;

import com.team03.monew.articleView.domain.NewsViews;
import com.team03.monew.articleView.repository.NewsViewsRepository;
import com.team03.monew.news.domain.News;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.repository.UserRepository;
import java.util.UUID;

public class BasicNewsViewsService implements NewsViewsService {

  private NewsViewsRepository newsViewsRepository;
  private UserRepository userRepository;

  @Override
  public boolean isRead(News news, UUID userId) {
    if(userId == null){
      return false;
    }

    boolean alreadyRead = newsViewsRepository.existsByArticleIdAndUserId(news.getId(), userId);
    //이미 뉴스를 읽었는지 확인
    if(alreadyRead){
      return true;
    }

    User user = userRepository.getReferenceById(userId);
    newsViewsRepository.save(new NewsViews(user,news));
    news.increaseViewCount();

    return true;
  }
}
