package com.team03.monew.common;

import com.team03.monew.common.domain.Press;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FetchedNews {
  private final Press source;
  private final String resourceLink;
  private final String title;
  private final LocalDateTime postDate;
  private final String overview;
}