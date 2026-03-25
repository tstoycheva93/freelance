package com.thesis.freelance.features.conversation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateConversationRequest(
    UUID otherUserId,
    
    @NotNull(message = "Offer ID is required")
    UUID offerId
) {}
