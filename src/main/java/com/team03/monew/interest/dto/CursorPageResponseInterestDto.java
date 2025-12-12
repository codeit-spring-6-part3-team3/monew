package com.team03.monew.interest.dto;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record CursorPageResponseInterestDto(
        List<InterestDto> content,
        String nextCursor,
        String nextAfter,
        int size,
        Long totalElements,
        Boolean hasNext
) {
    public CursorPageResponseInterestDto(){
        this(
                new ArrayList<>(),
                null,
                null,
                0,
                0L,
                false
        );
    }
}
