package com.team03.monew.article.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseArticleDto<T>(
    List<T> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {
  public static <T> CursorPageResponseArticleDto<T> of(
      List<T> content,
      String nextCursor,
      LocalDateTime nextAfter,
      int size,
      Long totalElements,
      boolean hasNext
  ){
    return new CursorPageResponseArticleDto<>(content, nextCursor, nextAfter, size,totalElements, hasNext);
  }
}
