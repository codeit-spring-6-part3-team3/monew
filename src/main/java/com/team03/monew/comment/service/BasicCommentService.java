package com.team03.monew.comment.service;

import com.team03.monew.comment.domain.Comment;
import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.repository.CommentRepository;
import com.team03.monew.commentlike.service.CommentLikeService;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicCommentService implements CommentService{

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CommentDto createComment(CommentRegisterRequest request) {
        Comment comment = Comment.of(
                request.articleId(),
                request.userId(),
                request.content()
        );

        Comment savedComment = commentRepository.save(comment);

        CommentDto convertComment = new CommentDto(
                savedComment.getId(),
                savedComment.getArticleId(),
                savedComment.getUserId(),
                null,
                savedComment.getContent(),
                savedComment.getLikeCount(),
                false,
                savedComment.getCreatedAt()
        );

        return convertComment;
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseCommentDto getCommentList(CursorPageRequestCommentDto request) {
        Slice<CommentDto> slice = commentRepository.findByCursor(request);

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (slice.hasNext() && slice.hasContent()) {
            List<CommentDto> content = slice.getContent();
            CommentDto lastComment = content.get(content.size() - 1);

            if ("like_count".equals(request.orderBy())) {
                nextCursor = String.valueOf(lastComment.likeCount());
            } else {
                nextCursor = lastComment.createdAt().toString();
            }
            nextAfter = lastComment.createdAt();
        }

        Long totalElements = null;
        if (request.cursor() == null) {
            totalElements = commentRepository.countByArticleIdAndDeletedAtIsNull(request.articleId());
        }

        return new CursorPageResponseCommentDto(
                slice,
                nextCursor,
                nextAfter,
                slice.getContent().size(),
                totalElements,
                slice.hasNext()
        );
    }

    @Transactional
    public void updateComment(UUID commentId, CommentUserIdRequest userId, CommentUpdateRequest content) {
        Comment comment = findById(commentId);

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글");
        }

        if (!comment.getUserId().equals(userId.userId())) {
            throw new IllegalArgumentException("본인 댓글만 가능");
        }

        comment.changeContent(content.content());
    }

    // 논리 삭제
    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = findById(commentId);

        comment.softDelete();
    }

    @Transactional
    public void deleteCommentHard(UUID commentId) {
        Comment comment = findById(commentId);

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findByIdAndUserId(UUID commentId, UUID userId) {
        Comment comment = findById(commentId);
        UserDto user = userService.findById(userId);

        return new CommentDto(
                comment.getId(),
                comment.getArticleId(),
                comment.getUserId(),
                user.nickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.isLikedByMe(),
                comment.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentActivityDto> topTenByUserId(UUID userId) {
        return commentRepository.findTopTenByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Comment findById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글 없음"));
    }

    @Override
    @Transactional
    public void increaseLikeCount(UUID commentId) {
        Comment comment = findById(commentId);
        comment.increaseLikeCount();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void decreaseLikeCount(UUID commentId) {
        Comment comment = findById(commentId);
        comment.decreaseLikeCount();
        commentRepository.save(comment);
    }
}
