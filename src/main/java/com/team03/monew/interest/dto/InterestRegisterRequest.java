package com.team03.monew.interest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record InterestRegisterRequest(
        @NotBlank
        @Size(min = 1, max = 50)
        String name,
        @NotEmpty
        @Size(min = 1, max = 10)
        List<String> keywords
) {
}
