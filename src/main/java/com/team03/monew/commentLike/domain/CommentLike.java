package com.team03.monew.commentLike.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "commentLikes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "likedBy", nullable = false)
    private UUID likedBy;

    @CreationTimestamp
    @Column(name = "creationAt", nullable = false, updatable = false)
    private LocalDateTime creationAt;

    @Column(name = "commentId", nullable = false)
    private UUID commentId;

    @Column(name = "articleId", nullable = false)
    private UUID articleId;

    @Column(name = "commentUserId", nullable = false)
    private UUID commentUserId;

    @Column(name = "commentUserNickname", nullable = false)
    private String commentUserNickname;

    @Column(name = "commentContent", nullable = false)
    private String commentContent;

    @Column(name = "commentLikeCount", nullable = false)
    private Long commentLikeCount;

    @Column(name = "commentCreatedAt", nullable = false)
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