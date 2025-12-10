package com.team03.monew.news.collect.domain;

import com.team03.monew.interest.domain.Interest;
import java.util.Set;

public record FilteredNewsTask(
    FetchedNews news,
    Set<Interest> matchedInterests
) {}