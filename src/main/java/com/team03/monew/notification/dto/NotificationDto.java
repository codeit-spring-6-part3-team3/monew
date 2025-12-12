package com.team03.monew.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team03.monew.notification.domain.NoticeResourceType;
import com.team03.monew.notification.domain.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @JsonProperty("confirmed") Boolean isChecked,
        UUID userId,
        @JsonProperty("content") String context,
        @JsonProperty("resourceType") NoticeResourceType resource,
        UUID resourceId
) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.isChecked(),
                notification.getUserId(),
                notification.getContext(),
                notification.getResource(),
                notification.getResourceId()
        );
    }
}