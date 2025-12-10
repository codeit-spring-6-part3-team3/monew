package com.team03.monew.article.dto;

import com.team03.monew.article.domain.ArticleSourceType;
import java.time.LocalDateTime;

public record ArticleCreateRequest(
    ArticleSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overView
) { }
