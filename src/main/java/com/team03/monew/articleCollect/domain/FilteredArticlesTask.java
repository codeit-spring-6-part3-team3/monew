package com.team03.monew.articleCollect.domain;

import com.team03.monew.interest.domain.Interest;
import java.util.Set;

public record FilteredArticlesTask(
    FetchedArticles article,
    Set<Interest> matchedInterests
) {}
