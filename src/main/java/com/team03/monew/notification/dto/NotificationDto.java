package com.team03.monew.notification.dto;

import com.team03.monew.notification.domain.NoticeResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        LocalDateTime creationAt,
        LocalDateTime updatedAt,
        Boolean isChecked,
        UUID userId,
        String context,
        NoticeResourceType resource,
        UUID resourceId
) {}