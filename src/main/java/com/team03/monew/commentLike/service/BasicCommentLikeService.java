package com.team03.monew.commentLike.service;

import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.service.CommentService;
import com.team03.monew.commentLike.domain.CommentLike;
import com.team03.monew.commentLike.dto.CommentLikeActivityDto;
import com.team03.monew.commentLike.repository.CommentLikeRepository;
import com.team03.monew.notification.domain.NoticeResourceType;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.notification.service.NotificationService;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicCommentLikeService implements CommentLikeService{

    private final CommentLikeRepository commentLikeRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void like(UUID commentId, UUID userId) {
        CommentDto comment = commentService.findByIdAndUserId(commentId, userId);
        UserDto user = userService.findById(userId);

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("이미 누름");
        }

        CommentLike commentLike = CommentLike.create(
                userId,
                commentId,
                comment.articleId(),
                comment.userId(),
                user.nickname(),
                comment.content(),
                commentLikeRepository.countCommentLikeByCommentId(commentId),
                comment.createdAt()
        );

        commentLikeRepository.save(commentLike);

        createCommentLikeNotification(comment.userId(), commentId, userId);
    }

    private void createCommentLikeNotification(UUID userId, UUID commentId, UUID likeUserId) {
        UserDto user = userService.findById(likeUserId);

        String content = user.nickname() + "님이 나의 댓글을 좋아합니다.";

        NotificationCreateDto notificationDto = NotificationCreateDto.builder()
                .userId(userId)
                .context(content)
                .resource(NoticeResourceType.REPLY)
                .resourceId(commentId)
                .build();

        notificationService.createNotification(notificationDto);
    }

    @Override
    @Transactional
    public void unlike(UUID commentId, UUID userId) {
        CommentDto comment = commentService.findByIdAndUserId(commentId, userId);

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("이미 없음");
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    @Override
    public Long countByCommentId(UUID commentId) {
        return commentLikeRepository.countCommentLikeByCommentId(commentId);
    }

    @Override
    public Boolean isLiked(UUID commentId, UUID userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    @Override
    public List<CommentLikeActivityDto> topTenByUserId(UUID userId) {
        return commentLikeRepository.findTopTenByUserIdOrderByCreationAtDesc(userId);
    }
}
