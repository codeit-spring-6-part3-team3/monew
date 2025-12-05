package com.team03.monew.comment.dto;

import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public record CursorPageResponseCommentDto(
        Slice<CommentDto> content,
        String nextCursor,
        LocalDateTime nextAfter,
        Integer size,
        Long totalElements,
        boolean hasNext
) {
}
