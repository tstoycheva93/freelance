package com.thesis.freelance.features.user.dto;

import java.util.UUID;

public record AdminDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Boolean isVerified
) {
}
