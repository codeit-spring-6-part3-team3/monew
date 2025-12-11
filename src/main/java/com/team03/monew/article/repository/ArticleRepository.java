package com.team03.monew.article.repository;

import com.team03.monew.article.domain.Article;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleQueryRepository {
  Optional<Article> findByResourceLink(String resourceLink);
  
  // Interest 삭제 전 참조 체크용
  boolean existsByInterestId(UUID interestId);
  
  // Interest 참조를 NULL로 업데이트할 때 사용
  List<Article> findByInterestId(UUID interestId);
}
