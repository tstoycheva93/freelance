package com.thesis.freelance.features.message.dto;

import java.util.List;
import java.util.UUID;

public record ReadStatusUpdateDto(
    String conversationId,
    List<UUID> messageIds,
    boolean isRead
) {}
