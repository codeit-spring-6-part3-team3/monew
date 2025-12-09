package com.team03.monew.comment.service;

import com.team03.monew.comment.dto.*;

import java.util.UUID;

public interface CommentService {
    CursorPageResponseCommentDto getCommentList(CursorPageRequestCommentDto request);
}
