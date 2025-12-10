package com.team03.monew.commentlike.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "comment_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"liked_by", "comment_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "liked_by", nullable = false)
    private UUID likedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(name = "comment_user_id", nullable = false)
    private UUID commentUserId;

    @Column(name = "comment_user_nickname", nullable = false)
    private String commentUserNickname;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Column(name = "comment_like_count", nullable = false)
    private Long commentLikeCount;

    @Column(name = "comment_created_at", nullable = false)
    private LocalDateTime commentCreatedAt;

    private CommentLike(
            UUID likedBy,
            UUID commentId,
            UUID articleId,
            UUID commentUserId,
            String commentUserNickname,
            String commentContent,
            Long commentLikeCount,
            LocalDateTime commentCreatedAt
    ) {
        this.likedBy = likedBy;
        this.commentId = commentId;
        this.articleId = articleId;
        this.commentUserId = commentUserId;
        this.commentUserNickname = commentUserNickname;
        this.commentContent = commentContent;
        this.commentLikeCount = commentLikeCount;
        this.commentCreatedAt = commentCreatedAt;
    }

    public static CommentLike create(
            UUID likedBy,
            UUID commentId,
            UUID articleId,
            UUID commentUserId,
            String commentUserNickname,
            String commentContent,
            Long commentLikeCount,
            LocalDateTime commentCreatedAt
    ) {
        return new CommentLike(
                likedBy,
                commentId,
                articleId,
                commentUserId,
                commentUserNickname,
                commentContent,
                commentLikeCount,
                commentCreatedAt
        );
    }
}