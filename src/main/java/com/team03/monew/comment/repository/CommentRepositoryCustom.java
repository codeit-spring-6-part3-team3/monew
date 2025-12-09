package com.team03.monew.comment.repository;

import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.dto.CursorPageRequestCommentDto;

import java.util.List;

public interface CommentRepositoryCustom {
    List<CommentDto> findByCursor(CursorPageRequestCommentDto request);
}
