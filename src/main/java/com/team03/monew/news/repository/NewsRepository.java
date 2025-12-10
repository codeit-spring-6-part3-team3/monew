package com.team03.monew.news.repository;

import com.team03.monew.news.domain.News;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
public interface NewsRepository extends JpaRepository<News, UUID>, NewsQueryRepository {
=======
public interface NewsRepository extends JpaRepository<News, UUID> {
>>>>>>> feature/collectnews
  Optional<News> findByResourceLink(String resourceLink);
}
