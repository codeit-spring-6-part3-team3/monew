package com.team03.monew.notification.dto;

import java.util.List;

public record CursorPageResponseNotificationDto(
        List<NotificationDto> content,
        String nextCursor,
        String nextAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {}