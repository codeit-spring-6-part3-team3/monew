package com.team03.monew.articleviews.dto;

import java.time.LocalDateTime;
import java.util.UUID;

//기사 뷰 등록에서 쓰이는것 같음
public record ArticleViewDto(
    UUID id,
    UUID viewedBy,
    LocalDateTime createdAt,
    UUID articleId,
    String source,
    String sourceUrl,
    String articleTitle,
    LocalDateTime articlePublishedDate,
    String articleSummary,
    Long articleCommentCount,
    Long articleViewCount

) {

}
