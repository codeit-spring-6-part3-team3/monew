package com.team03.monew.articleView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "articleViews",
    uniqueConstraints = {
    @UniqueConstraint(columnNames = {"viewedBy","articleId"})
  }
)
public class NewsViews {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(name = "viewedBy",updatable = false)
  UUID userId;

  @Column(name = "creationAt",updatable = false)
  LocalDateTime creationAt;

  @Column(name = "articleId",updatable = false)
  UUID articleId;

  public NewsViews(UUID userId, UUID articleId) {
    this.userId = userId;
    this.articleId = articleId;
  }

  @PrePersist
  public void OnCreate() {
    this.creationAt = LocalDateTime.now();
  }



}
