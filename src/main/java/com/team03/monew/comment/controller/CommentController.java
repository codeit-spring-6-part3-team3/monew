package com.team03.monew.comment.controller;

import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.dto.CommentRegisterRequest;
import com.team03.monew.comment.dto.CommentUpdateRequest;
import com.team03.monew.comment.dto.CommentUserIdRequest;
import com.team03.monew.comment.dto.CursorPageRequestCommentDto;
import com.team03.monew.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentDto> listRead(
            @RequestParam CursorPageRequestCommentDto request
    ) {
        return null;
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
