package com.team03.monew.news.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//jspecify의 nullmarked 사용하여 별도의 null여부 없으면 모두 nonNull로 간주
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// reosourceLink에 유니크 제약 조건을 걸어둠
@Table(name = "news",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "resourceLink")
  }
)
public class News {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID articleId;

  @Enumerated(EnumType.STRING)
  @Column(name = "source")
  private NewsSourceType source;

  @Column(name = "resourceLink")
  private String resourceLink;

  @Column(name= "title")
  private String title;

  @Column(name = "postDate")
  private LocalDateTime postDate;

  @Column(name = "overview")
  private String overview;

  @Column(name = "viewCount",columnDefinition = "INTEGER DEFAULT 0")
  private int viewCount;

  @Column(name = "creationAt", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updatedAt",nullable = true)
  private LocalDateTime updatedAt;

  @Column(name = "isDelete",columnDefinition = "boolean default false")
  private boolean isDelete;

  @Builder
  public News(
      NewsSourceType source,
      String resourceLink,
      String title,
      LocalDateTime postDate,
      String overview,
      boolean isDelete
  )
  {
    this.source = source;
    this.resourceLink = resourceLink;
    this.title = title;
    this.postDate = postDate;
    this.overview = overview;
    this.isDelete = isDelete;
  }

  // 읽은 수 증가
  public void increaseViewCount(){

  }

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public void deleteNews(){
    this.isDelete = true;
  }
}
