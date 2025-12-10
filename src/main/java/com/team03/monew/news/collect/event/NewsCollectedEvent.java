package com.team03.monew.news.collect.event;

import java.util.Set;
import java.util.UUID;

public record NewsCollectedEvent(
    UUID newsId,
    Set<UUID> interestIds
) {

}