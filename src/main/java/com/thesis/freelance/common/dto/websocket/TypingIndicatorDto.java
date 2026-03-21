package com.thesis.freelance.common.dto.websocket;

import java.util.UUID;

public record TypingIndicatorDto(
    UUID conversationId,
    UUID userId,
    String userName,
    boolean isTyping
) {}
