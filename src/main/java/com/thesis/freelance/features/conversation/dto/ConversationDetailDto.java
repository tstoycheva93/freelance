package com.thesis.freelance.features.conversation.dto;

import com.thesis.freelance.features.message.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public record ConversationDetailDto(
    UUID id,
    ParticipantDto participant,
    RelatedOfferDto relatedOffer,
    List<MessageDto> messages,
    int unreadCount
) {}
