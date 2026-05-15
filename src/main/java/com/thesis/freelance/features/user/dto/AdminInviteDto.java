package com.thesis.freelance.features.user.dto;

import com.thesis.freelance.features.user.entity.InviteStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminInviteDto(
        UUID id,
        String email,
        InviteStatus status,
        String invitedByName,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        LocalDateTime acceptedAt
) {
}
