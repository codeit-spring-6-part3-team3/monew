package com.team03.monew.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "liked_by_me", nullable = false)
    private boolean likedByMe;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 논리 삭제용 필드 추가
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    private Comment(
            UUID articleId,
            UUID userId,
            String content
    ) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
        this.likeCount = 0L;
        this.likedByMe = false;
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

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
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