package com.team03.monew.commentlike.dto;

import com.team03.monew.article.domain.Article;
import com.team03.monew.commentlike.domain.CommentLike;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentLikeActivityDto(
        UUID id,
        LocalDateTime createdAt,
        UUID commentId,
        UUID articleId,
        String articleTitle,
        UUID commentUserId,
        String commentUserNickname,
        String commentContent,
        Long commentLikeCount,
        LocalDateTime commentCreatedAt
) {
    public CommentLikeActivityDto(
            CommentLike commentLike,
            Article article
    ) {
        this(
                commentLike.getId(),
                commentLike.getCreatedAt(),
                commentLike.getCommentId(),
                article.getId(),
                article.getTitle(),
                commentLike.getCommentUserId(),
                commentLike.getCommentUserNickname(),
                commentLike.getCommentContent(),
                commentLike.getCommentLikeCount(),
                commentLike.getCommentCreatedAt()
        );
    }
}
