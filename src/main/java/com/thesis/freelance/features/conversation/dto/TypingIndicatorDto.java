package com.thesis.freelance.features.conversation.dto;

import java.util.UUID;

public record TypingIndicatorDto(
    UUID conversationId,
    UUID userId,
    String userName,
    boolean isTyping
) {}
