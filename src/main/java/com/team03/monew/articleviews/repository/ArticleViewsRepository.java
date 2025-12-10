package com.team03.monew.articleviews.repository;

import com.team03.monew.articleviews.domain.ArticleViews;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewsRepository extends JpaRepository<ArticleViews, UUID> {
  //사용자가 뉴스를 봤는지 확인하는 로직
  boolean existsByArticleIdAndUserId(UUID articleId,UUID userId);
}
