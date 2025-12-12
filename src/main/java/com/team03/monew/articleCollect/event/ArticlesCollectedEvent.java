package com.team03.monew.articleCollect.event;

import java.util.Set;
import java.util.UUID;

public record ArticlesCollectedEvent(
    UUID articleId,
    Set<UUID> interestIds
) {

}