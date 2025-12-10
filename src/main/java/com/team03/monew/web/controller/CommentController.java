package com.team03.monew.web.controller;

import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
            @RequestParam CursorPageRequestCommentDto request
    ) {
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
            @RequestParam CommentUserIdRequest request
    ) {
        commentService.likeComment(commentId, request.userId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{commentId}/comment-likes")
    public ResponseEntity<Void> unlike(
            @PathVariable UUID commentId,
            @RequestParam CommentUserIdRequest request
    ) {
        commentService.unlikeComment(commentId, request.userId());
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
            @RequestParam CommentUserIdRequest requestParam,
            @RequestBody CommentUpdateRequest requestBody
    ) {
        commentService.updateComment(commentId, requestParam, requestBody);
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
