package com.team03.monew.commentlike.service;

import com.team03.monew.commentlike.dto.CommentLikeActivityDto;

import java.util.List;
import java.util.UUID;

public interface CommentLikeService {
    void like(UUID commentId, UUID userId);
    void unlike(UUID commentId, UUID userId);
    Long countByCommentId(UUID commentId);
    Boolean isLiked(UUID commentId, UUID userId);
    List<CommentLikeActivityDto> topTenByUserId(UUID userId);
}
