package com.team03.monew.comment.dto;

import lombok.Builder;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CursorPageRequestCommentDto(
        UUID articleId,
        @DefaultValue("created_at") String orderBy,
        @DefaultValue("DESC") String direction,
        String cursor,
        LocalDateTime after,
        @DefaultValue("20") Integer limit,
        UUID userId
) {
}
