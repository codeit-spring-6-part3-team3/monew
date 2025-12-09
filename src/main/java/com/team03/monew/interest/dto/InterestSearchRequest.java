package com.team03.monew.interest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
@Builder
public record InterestSearchRequest(
        String keyword,
        @NotBlank
        String orderBy,
        @NotBlank
        String direction,
        String cursor,
        String after,
        @NotNull
        int limit
) {
}
