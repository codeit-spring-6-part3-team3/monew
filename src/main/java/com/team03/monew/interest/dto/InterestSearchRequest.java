package com.team03.monew.interest.dto;

import lombok.Builder;
@Builder
public record InterestSearchRequest(
        String keyword,
        String orderBy,
        String direction,
        String cursor,
        String after,
        int limit
) {
}
