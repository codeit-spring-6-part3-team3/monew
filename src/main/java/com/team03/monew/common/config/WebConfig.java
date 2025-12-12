package com.team03.monew.common.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @InitBinder
  public void initBinder(WebDataBinder binder) {

    // UUID 자동 변환 처리
    binder.registerCustomEditor(UUID.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) {
        if (text == null || text.isBlank()) {
          setValue(null);
        } else {
          setValue(UUID.fromString(text));
        }
      }
    });

    // LocalDateTime 자동 변환 처리
    binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
      @Override
      public void setAsText(String text) {
        if (text == null || text.isBlank()) {
          setValue(null);
        } else {
          setValue(LocalDateTime.parse(text));
        }
      }
    });
  }
}