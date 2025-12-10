package com.team03.monew.comment.service;

import com.team03.monew.comment.domain.Comment;
import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.repository.CommentRepository;
import com.team03.monew.commentlike.service.CommentLikeService;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicCommentService implements CommentService{

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
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
    public CursorPageResponseCommentDto getCommentList(CursorPageRequestCommentDto request) {
        int limit = request.limit() != null ? request.limit() : 20;

        CursorPageRequestCommentDto modifiedRequest = new CursorPageRequestCommentDto(
                request.articleId(),
                request.orderBy(),
                request.direction(),
                request.cursor(),
                request.after(),
                limit + 1,
                request.userId()
        );

        List<CommentDto> comments = commentRepository.findByCursor(modifiedRequest);

        boolean hasNext = comments.size() > limit;
        if (hasNext) {
            comments = comments.subList(0, limit);
        }

        String nextCursor = null;
        LocalDateTime nextAfter = null;
        if (hasNext && !comments.isEmpty()) {
            CommentDto lastComment = comments.get(comments.size() - 1);

            if ("likeCount".equals(request.orderBy())) {
                nextCursor = String.valueOf(lastComment.likeCount());
            } else {
                nextCursor = lastComment.createdAt().toString();
            }
            nextAfter = lastComment.createdAt();
        }

        Slice<CommentDto> slice = new SliceImpl<>(
                comments,
                PageRequest.of(0, limit),
                hasNext
        );

        Long totalElements = commentRepository.countByArticleIdAndDeletedAtIsNull(request.articleId());

        return new CursorPageResponseCommentDto(
                slice,
                nextCursor,
                nextAfter,
                comments.size(),
                totalElements,
                hasNext
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

    private Comment findById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글 없음"));
    }

    @Override
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
                commentLikeService.isLiked(commentId, userId),
                comment.getCreatedAt());
    }

    @Override
    @Transactional
    public void likeComment(UUID commentId, UUID userId) {
        Comment comment = findById(commentId);

        commentLikeService.like(commentId, userId);
        comment.changeLikeCount(commentLikeService.countByCommentId(commentId));
        comment.changeLikedByMe(commentLikeService.isLiked(commentId, userId));
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void unlikeComment(UUID commentId, UUID userId) {
        Comment comment = findById(commentId);

        commentLikeService.unlike(commentId, userId);
        comment.changeLikeCount(commentLikeService.countByCommentId(commentId));
        comment.changeLikedByMe(commentLikeService.isLiked(commentId, userId));
        commentRepository.save(comment);
    }

    @Override
    public List<CommentActivityDto> topTenByUserId(UUID userId) {
        return commentRepository.findTopTenByUserIdOrderByCreatedAtDesc(userId);
    }
}
