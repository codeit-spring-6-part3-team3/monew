package com.team03.monew.commentLike.service;

import com.team03.monew.commentLike.domain.CommentLike;
import com.team03.monew.commentLike.dto.CommentLikeActivityDto;

import java.util.List;
import java.util.UUID;

public interface CommentLikeService {
    List<CommentLikeActivityDto> topTenByUserId(UUID userId);
}
