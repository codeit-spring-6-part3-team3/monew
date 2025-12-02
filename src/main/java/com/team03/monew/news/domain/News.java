package com.team03.monew.news.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

//jspecify의 nullmarked 사용하여 별도의 null여부 없으면 모두 nonNull로 간주
// reosourceLink에 유니크 제약 조건을 걸어둠
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

  @Column(name = "resourceLink",length = 300)
  private String resourceLink;

  @Column(name= "title", length = 100)
  private String title;

  @Column(name = "postDate")
  private LocalDateTime postDate;

  @Column(name = "overview", length = 1000)
  private String overview;

  @Column(name = "viewCount",columnDefinition = "INTEGER DEFAULT 0")
  private int viewCount;

  @Column(name = "creationAt", updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updatedAt",nullable = true)
  @LastModifiedDate
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

  // 뉴스 논리 삭제
  public void deleteNews(){
    this.isDelete = true;
  }
}
