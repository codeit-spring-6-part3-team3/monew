package com.team03.monew.articleView.domain;

import com.team03.monew.news.domain.News;
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
    @UniqueConstraint(columnNames = {"user_id","article_id"})
  }
)
public class NewsViews {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "article_views_id")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "created_at",updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "article_id")
  private News news;


  public NewsViews(User  user, News news) {
    this.user = user;
    this.news = news;
  }

  @PrePersist
  public void OnCreate() {
    this.createdAt = LocalDateTime.now();
  }



}
