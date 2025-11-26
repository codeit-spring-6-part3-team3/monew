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
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

//jspecify의 nullmarked 사용하여 별도의 null여부 없으면 모두 nonNull로 간주
@Entity
@NullMarked
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "news")
public class News {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID articleId;

  @Enumerated(EnumType.STRING)
  @Column(name = "source")
  private NewsSource source;

  @Column(name = "resourceLink")
  private String resourceLink;

  @Column(name= "title")
  private String title;

  @Column(name = "postDate")
  private Date postDate;

  @Column(name = "overview")
  private String overview;

  @Column(name = "viewCount")
  private int viewCount;

  @Column(name = "creationAt")
  private LocalDateTime createdAt;

  @Column(name = "updatedAt",nullable = true)
  private LocalDateTime updatedAt;


  @Builder
  public News(
      NewsSource source,
      String resourceLink,
      String title,
      Date postDate,
      String overview
  )
  {
    this.source = source;
    this.resourceLink = resourceLink;
    this.title = title;
    this.postDate = postDate;
    this.overview = overview;

    // 읽은 수 0으로 디폴트
    this.viewCount = 0;
  }

  // 읽은 수 증가
  public void increaseViewCount(){
    this.viewCount++;
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

}
