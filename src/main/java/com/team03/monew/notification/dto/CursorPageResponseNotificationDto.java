package com.team03.monew.notification.dto;

import java.util.List;

public record CursorPageResponseNotificationDto(
        List<NotificationDto> content,
        String nextCursor,
        String nextAfter,
        Integer size,
        Integer totalElements,
        Boolean hasNext
) {}