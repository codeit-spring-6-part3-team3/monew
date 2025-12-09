package com.team03.monew.news.domain;

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

  // 크기 늘리는것이 필요할것 같음
  @Column(name = "resourceLink",length = 300)
  private String resourceLink;


  // 이부분도 늘리는것이 좋을것 같음
  @Column(name= "title", length = 100)
  private String title;

  //* -> 현재 동사형이고 나머지는 과거로 되어있어서 일관성이 없다 -> 확인해보아라
  // postedAt으로 표기. ->  컨벤션들이 일관
  @Column(name = "postDate")
  private LocalDateTime postDate;

  @Column(name = "overview", length = 1000)
  private String overview;

  @Column(name = "viewCount",columnDefinition = "INTEGER DEFAULT 0")
  private long viewCount;

  @Column(name = "commentCount",columnDefinition = "INTEGER DEFAULT 0")
  private long commentCount;

  // * 컬럼명은 동사 과거형으로 써 두는것이 디폴트
  @Column(name = "creationAt", updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updatedAt",nullable = true)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column(name = "isDelete",columnDefinition = "boolean default false")
  private boolean isDelete;

  @ManyToOne
  @JoinColumn(name = "interestId")
  private Interest interest;

  @Builder
  public News(
      NewsSourceType source,
      String resourceLink,
      String title,
      LocalDateTime postDate,
      String overview,
      long viewCount,
      long commentCount,
      boolean isDelete
  )
  {
    this.source = source;
    this.resourceLink = resourceLink;
    this.title = title;
    this.postDate = postDate;
    this.overview = overview;
    this.viewCount = viewCount;
    this.commentCount = commentCount;
    this.isDelete = isDelete;
  }

  // 읽은 수 증가
  public void increaseViewCount(){
    this.viewCount++;
  }

  // 댓글 수 증가
  public void increaseCommentCount(){
    this.commentCount++;
  }

  // 뉴스 논리 삭제
  public void deleteNews(){
    this.isDelete = true;
  }
}
