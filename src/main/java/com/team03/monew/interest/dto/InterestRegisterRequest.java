package com.team03.monew.interest.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record InterestRegisterRequest(
        @Size(min = 1, max = 50)
        String name,
        @Size(min = 1, max = 10)
        List<String> keywords
) {
}
