package com.team03.monew.articleCollect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.articleCollect.domain.FetchedArticles;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordFilterService {

  private final InterestRepository interestRepository;
  private volatile Map<String, Set<Interest>> keywordToInterests = Map.of();

  @PostConstruct
  public void init() {
    refresh();
  }

  @Scheduled(cron = "0 50 * * * *")
  public void refreshHourly() {
    refresh();
  }

  public void refresh() {
    try {
      Map<String, Set<Interest>> index = new HashMap<>();
      List<Interest> interests = interestRepository.findAll();

      for (Interest interest : interests) {
        List<String> keywords = interest.getKeywords();
        if (keywords == null) {
          continue;
        }

        for (String keyword : keywords) {
          String normalized = normalize(keyword);
          if (normalized == null) {
            continue;
          }

          index
              .computeIfAbsent(normalized, k -> new HashSet<>())
              .add(interest);
        }
      }

      this.keywordToInterests = Map.copyOf(
          index.entrySet().stream()
              .collect(Collectors.toMap(
                  Map.Entry::getKey,
                  e -> Set.copyOf(e.getValue())
              )));

      log.info("Keyword index refreshed. {} keywords loaded.", keywordToInterests.size());
    } catch (Exception e) {
      log.error("Failed to refresh keyword index", e);
    }
  }

  public Set<Interest> matchingInterests(FetchedArticles article) {
    if (article == null || keywordToInterests.isEmpty()) {
      return Set.of();
    }

    String title = normalize(Objects.toString(article.title(), ""));
    String overview = normalize(Objects.toString(article.overview(), ""));

    Set<Interest> result = new HashSet<>();

    for (Map.Entry<String, Set<Interest>> entry : keywordToInterests.entrySet()) {
      String keyword = entry.getKey();
      if (title.contains(keyword) || overview.contains(keyword)) {
        result.addAll(entry.getValue());
      }
    }

    return result;
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim().toLowerCase();
    return trimmed.isEmpty() ? null : trimmed;
  }
}