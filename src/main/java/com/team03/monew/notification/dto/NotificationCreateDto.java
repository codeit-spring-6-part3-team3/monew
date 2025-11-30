package com.team03.monew.notification.dto;

import com.team03.monew.notification.domain.NoticeResourceType;

import java.util.UUID;

public record NotificationCreateDto(
        UUID userId,
        String context,
        NoticeResourceType resource,
        UUID resourceId
) {}