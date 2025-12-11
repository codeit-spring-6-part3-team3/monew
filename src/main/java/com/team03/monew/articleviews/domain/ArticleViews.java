package com.team03.monew.articleviews.domain;

import com.team03.monew.article.domain.Article;
import com.team03.monew.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Entity
@NoArgsConstructor

/**
 * 유니크 제약 조건을
 * 읽은 사용자와, 뉴스기사를 결합하여
 *  중복된 결과가 나오지않도록 함으로
 *  같은사용자가 여러번 조회해도 조회수 증가하지 않고자함
 *  **/
@Table(name = "article_views",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"viewed_by","article_id"})
    }
)
@Getter
public class ArticleViews {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "article_views_id")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "viewed_by")
  private User user;

  @Column(name = "created_at",updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "article_id")
  private Article article;


  public ArticleViews(User  user, Article article) {
    this.user = user;
    this.article = article;
  }

  @PrePersist
  public void OnCreate() {
    this.createdAt = LocalDateTime.now();
  }



}