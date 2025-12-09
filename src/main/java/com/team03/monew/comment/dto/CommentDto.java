package com.team03.monew.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto (
        UUID id,
        UUID articleId,
        UUID userId,
        String userNickname,
        String content,
        Long likeCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {
}
