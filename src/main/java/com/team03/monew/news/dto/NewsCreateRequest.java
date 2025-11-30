package com.team03.monew.news.dto;

import com.team03.monew.news.domain.NewsSourceType;
import java.time.LocalDateTime;

public record NewsCreateRequest(
    NewsSourceType source,
    String resourceLink,
    String title,
    LocalDateTime postDate,
    String overView
) { }
