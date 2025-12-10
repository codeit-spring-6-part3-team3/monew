package com.team03.monew.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "articleId", nullable = false)
    private UUID articleId;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @CreationTimestamp
    @Column(name = "creationAt", nullable = false, updatable = false)
    private LocalDateTime creationAt;

    @Column(name = "likeCount", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Long likeCount;

    @Column(name = "likedByMe", nullable = false, columnDefinition = "FALSE")
    private boolean likedByMe;

    @UpdateTimestamp
    @Column(name = "updateAt", nullable = false)
    private LocalDateTime updateAt;

    // 논리 삭제용 필드 추가
    @Column(name = "deletedAt", nullable = true)
    private LocalDateTime deletedAt;

    private Comment(
            UUID articleId,
            UUID userId,
            String content
    ) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
    }

    public static Comment of(
            UUID articleId,
            UUID userId,
            String content
    ) {
        return new Comment(articleId, userId, content);
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void changeLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}