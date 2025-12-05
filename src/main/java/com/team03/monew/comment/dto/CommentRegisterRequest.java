package com.team03.monew.comment.dto;

import java.util.UUID;

public record CommentRegisterRequest(
        UUID articleId,
        UUID userId,
        String content
) {
}
