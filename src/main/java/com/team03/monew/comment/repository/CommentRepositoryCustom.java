package com.team03.monew.comment.repository;

import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.dto.CursorPageRequestCommentDto;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {
    Slice<CommentDto> findByCursor(CursorPageRequestCommentDto request);
}
