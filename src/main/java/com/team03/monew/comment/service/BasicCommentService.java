package com.team03.monew.comment.service;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.repository.ArticleRepository;
import com.team03.monew.comment.domain.Comment;
import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.repository.CommentRepository;
import com.team03.monew.commentlike.service.CommentLikeService;
import com.team03.monew.user.domain.User;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.repository.UserRepository;
import com.team03.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicCommentService implements CommentService{

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
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

        List<CommentDto> content = new ArrayList<>();
        if (!slice.getContent().isEmpty()) {
            content = slice.getContent();
        }

        return new CursorPageResponseCommentDto(
                content,
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
        List<Comment> commentList = commentRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        if (commentList.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> articleIds = commentList.stream()
                .map(Comment::getArticleId)
                .toList();

        List<UUID> userIds = commentList.stream()
                .map(Comment::getUserId)
                .toList();

        Map<UUID,Article> articleMap = articleRepository.findAllById(articleIds).stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));

        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return toDto(commentList,articleMap,userMap);
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
        comment.changeLikedByMe(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void decreaseLikeCount(UUID commentId) {
        Comment comment = findById(commentId);
        comment.decreaseLikeCount();
        comment.changeLikedByMe(false);
        commentRepository.save(comment);
    }

    private List<CommentActivityDto> toDto (
            List<Comment> commentList,
            Map<UUID, Article> articleMap,
            Map<UUID, User> userMap
    ) {
        List<CommentActivityDto> commentActivityDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Article article = articleMap.get(comment.getArticleId());
            User user = userMap.get(comment.getUserId());
            commentActivityDtoList.add(new CommentActivityDto(comment, article, user));
        }
        return commentActivityDtoList;
    }
}
