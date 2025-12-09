package com.team03.monew.comment.service;

import com.team03.monew.comment.dto.*;

import java.util.UUID;

public interface CommentService {
    CommentDto createComment(CommentRegisterRequest request);
    CursorPageResponseCommentDto getCommentList(CursorPageRequestCommentDto request);
    void updateComment(UUID commentId, CommentUserIdRequest userId, CommentUpdateRequest content);
    void deleteComment(UUID commentId);
    void deleteCommentHard(UUID commentId);
}
