package com.team03.monew.comment.service;

import com.team03.monew.comment.domain.Comment;
import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.repository.CommentRepository;
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
}
