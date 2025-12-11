package com.team03.monew.articleviews.repository;

import com.team03.monew.articleviews.domain.ArticleViews;

import java.util.List;
import java.util.UUID;

import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewsRepository extends JpaRepository<ArticleViews, UUID> {
  //사용자가 뉴스를 봤는지 확인하는 로직
  boolean existsByArticleIdAndUserId(UUID articleId,UUID userId);

  // 최근 좋아요 조회 (최대 10개)
  @Query("SELECT av FROM ArticleViews av WHERE av.viewed_by = :userId ORDER BY av.created_at DESC LIMIT 10")
  List<ArticleViewsActivityDto> findTopTenByUserIdOrderByCreatedAtDesc(
          @Param("user_id") UUID userId
  );
}
