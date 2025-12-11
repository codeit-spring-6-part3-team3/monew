package com.team03.monew.articleCollect.infrastructure.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}