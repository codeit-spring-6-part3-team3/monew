package com.team03.monew.notification.dto;

import java.util.UUID;

public record NotificationCheckDto(
        UUID notificationId,
        UUID userId
) {}