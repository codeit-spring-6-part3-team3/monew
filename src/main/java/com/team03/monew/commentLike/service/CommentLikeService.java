package com.team03.monew.commentLike.service;

import com.team03.monew.commentLike.domain.CommentLike;
import com.team03.monew.commentLike.dto.CommentLikeActivityDto;

import java.util.List;
import java.util.UUID;

public interface CommentLikeService {
    void like(UUID commentId, UUID userId);
    void unlike(UUID commentId, UUID userId);
    Long countByCommentId(UUID commentId);
    Boolean isLiked(UUID commentId, UUID userId);
    List<CommentLikeActivityDto> topTenByUserId(UUID userId);
}
