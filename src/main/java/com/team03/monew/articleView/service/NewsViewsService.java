package com.team03.monew.articleView.service;

import com.team03.monew.news.domain.News;
import java.util.UUID;

public interface NewsViewsService {
  boolean isRead(News news, UUID userId);
}
