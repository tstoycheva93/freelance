package com.thesis.freelance.features.conversation.dto;

import java.util.UUID;

public record ParticipantDto(
    UUID id,
    UUID userId,
    String name,
    String surname,
    String photoUrl
) {}
