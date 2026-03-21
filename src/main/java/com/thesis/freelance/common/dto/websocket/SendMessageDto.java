package com.thesis.freelance.common.dto.websocket;

import java.util.UUID;

public record SendMessageDto(
    UUID conversationId,
    String content,
    UUID clientMessageId
) {}
