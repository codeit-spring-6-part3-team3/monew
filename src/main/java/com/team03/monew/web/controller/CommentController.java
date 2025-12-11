package com.team03.monew.web.controller;

import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseCommentDto> listRead(
            @RequestParam(required = false) UUID articleId,
            @RequestParam String orderBy,
            @RequestParam String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) LocalDateTime after,
            @RequestParam Integer limit,
            @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ) {

        CursorPageRequestCommentDto request = CursorPageRequestCommentDto.builder()
                .articleId(articleId)
                .orderBy(orderBy)
                .direction(direction)
                .cursor(cursor)
                .after(after)
                .limit(limit)
                .userId(userId)
                .build();
        CursorPageResponseCommentDto response = commentService.getCommentList(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(
            @RequestBody CommentRegisterRequest request
    ) {
        CommentDto response = commentService.createComment(request);
        return ResponseEntity.created(URI.create("/api/comments/" + response.id())).body(response);
    }

    @PostMapping("{commentId}/comment-likes")
    public ResponseEntity<Void> like(
            @PathVariable UUID commentId,
            @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ) {
        commentService.increaseLikeCount(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{commentId}/comment-likes")
    public ResponseEntity<Void> unlike(
            @PathVariable UUID commentId,
            @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ) {
        commentService.decreaseLikeCount(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<Void> update(
            @PathVariable UUID commentId,
            @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
            @RequestBody CommentUpdateRequest requestBody
    ) {
        CommentUserIdRequest commentUserIdRequest = new CommentUserIdRequest(userId);
        commentService.updateComment(commentId, commentUserIdRequest, requestBody);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{commentId}/hard")
    public ResponseEntity<Void> deleteHard(
            @PathVariable UUID commentId
    ) {
        commentService.deleteCommentHard(commentId);
        return ResponseEntity.noContent().build();
    }
}
