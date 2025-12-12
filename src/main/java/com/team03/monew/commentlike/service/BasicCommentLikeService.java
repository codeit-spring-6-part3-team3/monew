package com.team03.monew.commentlike.service;

import com.team03.monew.article.domain.Article;
import com.team03.monew.article.repository.ArticleRepository;
import com.team03.monew.article.service.ArticleService;
import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.service.CommentService;
import com.team03.monew.commentlike.domain.CommentLike;
import com.team03.monew.commentlike.dto.CommentLikeActivityDto;
import com.team03.monew.commentlike.repository.CommentLikeRepository;
import com.team03.monew.notification.domain.NoticeResourceType;
import com.team03.monew.notification.dto.NotificationCreateDto;
import com.team03.monew.notification.service.NotificationService;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicCommentLikeService implements CommentLikeService{

    private final CommentLikeRepository commentLikeRepository;
    private final ArticleRepository articleRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void like(UUID commentId, UUID userId) {
        if (commentLikeRepository.existsByCommentIdAndCommentUserId(commentId, userId)) {
            throw new IllegalArgumentException("이미 좋아요");
        }

        CommentDto comment = commentService.findByIdAndUserId(commentId, userId);
        UserDto user = userService.findById(userId);

        Long currentCount = commentLikeRepository.countCommentLikeByCommentId(commentId);

        CommentLike commentLike = CommentLike.create(
                userId,
                commentId,
                comment.articleId(),
                comment.userId(),
                user.nickname(),
                comment.content(),
                currentCount + 1,
                comment.createdAt()
        );

        commentLikeRepository.save(commentLike);
        commentService.increaseLikeCount(commentId);
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
        if (!commentLikeRepository.existsByCommentIdAndCommentUserId(commentId, userId)) {
            throw new IllegalArgumentException("이미 없음");
        }

        commentLikeRepository.deleteByCommentIdAndCommentUserId(commentId, userId);
        commentService.decreaseLikeCount(commentId);
    }

    @Override
    public Long countByCommentId(UUID commentId) {
        return commentLikeRepository.countCommentLikeByCommentId(commentId);
    }

    @Override
    public Boolean isLiked(UUID commentId, UUID userId) {
        return commentLikeRepository.existsByCommentIdAndCommentUserId(commentId, userId);
    }

    @Override
    public List<CommentLikeActivityDto> topTenByUserId(UUID userId) {
        List<CommentLike> commentLikeList = commentLikeRepository.findTop10ByLikedByOrderByCreatedAtDesc(userId);

        if (commentLikeList.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> articleIdList = commentLikeList.stream()
                .map(CommentLike::getArticleId)
                .toList();

        Map<UUID,Article> articleList = articleRepository.findAllById(articleIdList).stream()
                .collect(Collectors.toMap(Article::getId, Function.identity()));

        return toDtoList(commentLikeList,articleList);
    }

    private List<CommentLikeActivityDto> toDtoList(List<CommentLike> commentLikeList,Map<UUID,Article>  articleList) {
        List<CommentLikeActivityDto> dtoList = new ArrayList<>();
        for (CommentLike commentLike : commentLikeList) {
            Article article = articleList.get(commentLike.getArticleId());
            CommentLikeActivityDto commentLikeActivityDto = new CommentLikeActivityDto(commentLike, article);
            dtoList.add(commentLikeActivityDto);
        }
        return dtoList;
    }
}
