package com.team03.monew.web.controller;

import com.team03.monew.articleviews.dto.ArticleViewsActivityDto;
import com.team03.monew.articleviews.service.ArticleViewsService;
import com.team03.monew.comment.dto.CommentActivityDto;
import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.service.CommentService;
import com.team03.monew.commentlike.dto.CommentLikeActivityDto;
import com.team03.monew.commentlike.service.CommentLikeService;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.user.dto.UserActivityDto;
import com.team03.monew.user.dto.UserDto;
import com.team03.monew.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class UserActivitiesController {
    UserService userService;
    SubscribeService subscribeService;
    CommentService commentService;
    CommentLikeService commentLikeService;
    ArticleViewsService articleViewsService;

    UserActivitiesController(UserService userService, SubscribeService subscribeService, CommentService commentService, CommentLikeService commentLikeService, ArticleViewsService articleViewsService) {
        this.userService = userService;
        this.subscribeService = subscribeService;
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
        this.articleViewsService = articleViewsService;
    }

    @GetMapping("/api/user-activities/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivities(
            @PathVariable UUID userId
    ) {
        UserDto user = userService.findById(userId);
        List<SubscribeDto> subscribes = subscribeService.subscribeUser(userId);
        List<CommentActivityDto> comments = commentService.topTenByUserId(userId);
        List<CommentLikeActivityDto> likes = commentLikeService.topTenByUserId(userId);
        List<ArticleViewsActivityDto> articles = articleViewsService.topTenByUserId(userId);

        UserActivityDto response = new UserActivityDto(user, subscribes, comments, likes, articles);

        return ResponseEntity.ok(response);
    }

}
