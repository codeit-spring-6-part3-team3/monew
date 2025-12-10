package com.team03.monew.article.repository;

import com.team03.monew.article.domain.Article;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleQueryRepository {
  Optional<Article> findByResourceLink(String resourceLink);
}
