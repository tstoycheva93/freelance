package com.thesis.freelance.features.conversation.dto;

import java.util.UUID;

public record SendMessageDto(
    UUID conversationId,
    String content,
    UUID clientMessageId
) {}
