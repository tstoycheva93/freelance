package com.thesis.freelance.features.notification.dto;

import com.thesis.freelance.common.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    NotificationType type,
    String title,
    String content,
    String pageUrl,
    boolean isRead,
    LocalDateTime createdAt
) {}
