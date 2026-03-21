package com.thesis.freelance.features.conversation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateConversationRequest(
    @NotNull(message = "Other user ID is required")
    UUID otherUserId,
    
    UUID offerId
) {}
