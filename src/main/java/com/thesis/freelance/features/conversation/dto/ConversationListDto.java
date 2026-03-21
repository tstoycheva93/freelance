package com.thesis.freelance.features.conversation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationListDto(
    UUID id,
    ParticipantDto participant,
    String lastMessage,
    LocalDateTime lastMessageTime,
    int unreadCount,
    RelatedOfferDto relatedOffer
) {}
