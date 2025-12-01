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

    @CreationTimestamp
    @Column(name = "creationAt", nullable = false, updatable = false)
    private LocalDateTime creationAt;

    @Column(name = "commentId", nullable = false)
    private UUID commentId;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    private CommentLike(
            UUID commentId,
            UUID userId
    ) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public static CommentLike create(
            UUID commentId,
            UUID userId
    ) {
        return new CommentLike(commentId, userId);
    }
}