package com.team03.monew.user.dto;

import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;
import com.team03.monew.comment.dto.CommentActivityDto;
import com.team03.monew.commentlike.dto.CommentLikeActivityDto;
import com.team03.monew.subscribe.dto.SubscribeDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
        UUID id,
        String email,
        String nickname,
        LocalDateTime createdAt,
        List<SubscribeDto> subscriptions,
        List<CommentActivityDto> comments,
        List<CommentLikeActivityDto> commentLikes,
        List<ArticleViewsActivityDto> articleViews
) {
    public UserActivityDto(
            UserDto user,
            List<SubscribeDto> subscribes,
            List<CommentActivityDto> comments,
            List<CommentLikeActivityDto> likes,
            List<ArticleViewsActivityDto> articles
    ) {
        this(
                user.id(),
                user.email(),
                user.nickname(),
                user.createdAt(),
                subscribes,
                comments,
                likes,
                articles
        );
    }
}
