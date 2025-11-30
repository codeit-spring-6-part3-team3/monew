package com.team03.monew.news.dto;

import java.util.UUID;

public record NewsDto(
    UUID id,
    String source,
    String sourceUrl,
    String title
) { }
