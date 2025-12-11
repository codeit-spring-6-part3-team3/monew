package com.team03.monew.comment.dto;

import com.team03.monew.article.domain.Article;
import com.team03.monew.comment.domain.Comment;
import com.team03.monew.user.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentActivityDto(
        UUID id,
        UUID articleId,
        String articleTitle,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        LocalDateTime createdAt
) {
    public CommentActivityDto(Comment comment , Article  article , User user) {
        this(
                comment.getId(),
                article.getId(),
                article.getTitle(),
                user.getId(),
                user.getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
        );
    }
}
