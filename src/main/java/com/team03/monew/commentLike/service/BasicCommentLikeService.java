package com.team03.monew.commentLike.service;

import com.team03.monew.commentLike.dto.CommentLikeActivityDto;
import com.team03.monew.commentLike.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicCommentLikeService implements CommentLikeService{

    private final CommentLikeRepository commentLikeRepository;

    @Override
    public List<CommentLikeActivityDto> topTenByUserId(UUID userId) {
        return commentLikeRepository.findTopTenByUserIdOrderByCreationAtDesc(userId);
    }
}
