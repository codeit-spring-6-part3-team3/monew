package com.team03.monew.commentLike.dto;

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
}
