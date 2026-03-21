package com.thesis.freelance.common.dto.websocket;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageWsDto(
    UUID id,
    UUID conversationId,
    UUID senderId,
    String senderName,
    String content,
    LocalDateTime sentAt,
    UUID clientMessageId
) {}
