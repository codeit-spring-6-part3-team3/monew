package com.team03.monew.comment.controller;

import com.team03.monew.comment.dto.*;
import com.team03.monew.comment.service.CommentService;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return null;
    }

    @PostMapping("{commentId}/comment-likes")
    public ResponseEntity<CommentDto> like(
            @PathVariable UUID commentId,
            @RequestParam CommentUserIdRequest request
    ) {
        return null;
    }

    @DeleteMapping("{commentId}/comment-likes")
    public ResponseEntity<CommentDto> unlike(
            @PathVariable UUID commentId,
            @RequestParam CommentUserIdRequest request
    ) {
        return null;
    }

    @DeleteMapping("{commentId}")
    public void delete(
            @PathVariable UUID commentId
    ) {}

    @PatchMapping("{commentId}")
    public void update(
            @PathVariable UUID commentId,
            @RequestParam CommentUserIdRequest requestParam,
            @RequestBody CommentUpdateRequest requestBody
    ) {}

    @DeleteMapping("{commentId}/hard")
    public void deleteHard(
            @PathVariable UUID commentId
    ) {}
}
