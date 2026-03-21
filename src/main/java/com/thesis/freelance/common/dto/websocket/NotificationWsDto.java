package com.thesis.freelance.common.dto.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thesis.freelance.common.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationWsDto(
    UUID id,
    NotificationType type,
    String title,
    String content,
    String pageUrl,
    boolean isRead,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt
) {}
