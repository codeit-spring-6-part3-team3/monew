package com.team03.monew.news.collect.service;

import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.repository.InterestRepository;
import com.team03.monew.news.collect.domain.FetchedNews;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordFilterService {

  private final InterestRepository interestRepository;
  private Map<String, Set<Interest>> keywordToInterests = Map.of();

  @PostConstruct
  public void init() {
    refresh();
  }

  public void refresh() {
    Map<String, Set<Interest>> index = new HashMap<>();
    List<Interest> interests = interestRepository.findAll();

    for (Interest interest : interests) {
      List<String> keywords = interest.getKeywords();
      if (keywords == null) {
        continue;
      }

      for (String keyword : keywords) {
        if (keyword == null) {
          continue;
        }
        String normalized = keyword.trim();
        if (normalized.isEmpty()) {
          continue;
        }

        index
            .computeIfAbsent(normalized, k -> new HashSet<>())
            .add(interest);
      }
    }

    this.keywordToInterests = index;
  }

  public Set<Interest> matchingInterests(FetchedNews news) {
    if (news == null || keywordToInterests.isEmpty()) {
      return Set.of();
    }

    String title = Objects.toString(news.title(), "");
    String overview = Objects.toString(news.overview(), "");

    Set<Interest> result = new HashSet<>();

    for (Map.Entry<String, Set<Interest>> entry : keywordToInterests.entrySet()) {
      String keyword = entry.getKey();
      if (title.contains(keyword) || overview.contains(keyword)) {
        result.addAll(entry.getValue());
      }
    }

    return result;
  }
}