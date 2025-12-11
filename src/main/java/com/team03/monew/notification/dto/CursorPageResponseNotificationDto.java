package com.team03.monew.notification.dto;

import com.team03.monew.notification.domain.Notification;
import org.springframework.data.domain.Slice;

import java.util.List;

public record CursorPageResponseNotificationDto(
        List<NotificationDto> content,
        String nextCursor,
        String nextAfter,
        Integer size,
        Long totalElements,
        Boolean hasNext
) {
    public static CursorPageResponseNotificationDto from(Slice<Notification> slice, int size) {
        List<NotificationDto> content = slice.getContent().stream()
                .map(NotificationDto::from)
                .toList();

        String nextCursor = null;
        String nextAfter = null;

        if (slice.hasNext() && !content.isEmpty()) {
            NotificationDto lastDto = content.get(content.size() - 1);
            nextCursor = lastDto.createdAt().toString();
            nextAfter = lastDto.createdAt().toString();
        }

        return new CursorPageResponseNotificationDto(
                content,
                nextCursor,
                nextAfter,
                size,
                null,  // Slice는 totalElements 제공 안 함
                slice.hasNext()
        );
    }
}