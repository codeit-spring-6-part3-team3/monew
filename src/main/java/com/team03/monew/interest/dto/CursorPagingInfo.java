package com.team03.monew.interest.dto;


public record CursorPagingInfo (
        String nextCursor,
        String nextAfter,
        Boolean hasNext
) {
}
