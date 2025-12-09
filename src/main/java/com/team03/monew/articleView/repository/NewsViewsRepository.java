package com.team03.monew.articleView.repository;

import com.team03.monew.articleView.domain.NewsViews;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsViewsRepository extends JpaRepository<NewsViews, UUID> {
  //사용자가 뉴스를 봤는지 확인하는 로직
  boolean existsByArticleIdAndUserId(UUID articleId,UUID userId);
}
