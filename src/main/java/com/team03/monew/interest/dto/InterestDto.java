package com.team03.monew.interest.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record InterestDto (
        UUID id,
        String name,
        List<String> keywords,
        Long subscriberCount,
        Boolean subscribedByMe
){
}
