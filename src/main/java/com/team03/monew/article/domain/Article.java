package com.team03.monew.article.domain;

import com.team03.monew.interest.domain.Interest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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


/**
 * 1. 디비 칼럼명 카멜케이스 --> 디비 에서는 카멜케이스를 안쓰고 스네이크로 작성 --> 디비에서는 대소문자 구분이 안되어서
 *
 * 2.
 *
 * **/

//jspecify의 nullmarked 사용하여 별도의 null여부 없으면 모두 nonNull로 간주
// reosourceLink에 유니크 제약 조건을 걸어둠
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "article",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "resource_link")
  }
)
public class Article {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "source")
  private ArticleSourceType source;

  // 크기 늘리는것이 필요할것 같음
  @Column(name = "resource_link",length = 600)
  private String resourceLink;


  // 이부분도 늘리는것이 좋을것 같음
  @Column(name= "title", length = 200)
  private String title;

  //* -> 현재 동사형이고 나머지는 과거로 되어있어서 일관성이 없다 -> 확인해보아라
  // postedAt으로 표기. ->  컨벤션들이 일관
  @Column(name = "posted_at")
  private LocalDateTime postedAt;

  @Column(name = "overview", length = 2000)
  private String overview;

  @Column(name = "view_count",columnDefinition = "INTEGER DEFAULT 0")
  private long viewCount;

  @Column(name = "comment_count",columnDefinition = "INTEGER DEFAULT 0")
  private long commentCount;

  // * 컬럼명은 동사 과거형으로 써 두는것이 디폴트
  @Column(name = "created_at", updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at",nullable = true)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "is_delete",columnDefinition = "boolean default false")
  private boolean isDelete;

  @ManyToOne
  @JoinColumn(name = "interest_id")
  private Interest interest;

  @Builder
  public Article(
      ArticleSourceType source,
      String resourceLink,
      String title,
      LocalDateTime postedAt,
      String overview,
      long viewCount,
      long commentCount,
      boolean isDelete,
      Interest interest
  )
  {
    this.source = source;
    this.resourceLink = resourceLink;
    this.title = title;
    this.postedAt = postedAt;
    this.overview = overview;
    this.viewCount = viewCount;
    this.commentCount = commentCount;
    this.isDelete = isDelete;
    this.interest = interest;
  }

  // 읽은 수 증가
  public void increaseViewCount(){
    this.viewCount++;
  }

  // 댓글 수 증가
  public void increaseCommentCount(){
    this.commentCount++;
  }

  // 댓글 수 감소
  public void decreaseCommentCount(){
   if(this.commentCount > 0){
     this.commentCount--;
   }
  }

  // 뉴스 논리 삭제
  public void deleteArticle(){
    this.isDelete = true;
  }
}
