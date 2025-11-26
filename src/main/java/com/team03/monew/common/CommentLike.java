package com.team03.monew.common;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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

}