package com.team03.monew.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CursorPageRequestCommentDto(
        UUID articleId,
        String orderBy,
        String direction,
        String cursor,
        LocalDateTime after,
        Integer limit,
        UUID userId
) {
}
