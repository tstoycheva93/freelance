package com.thesis.freelance.features.client.dto;

import java.util.UUID;

public record ClientPersonalDto(
        UUID id,
        String occupation,
        String companyName,
        String bio,
        String website,
        Integer completeness
) {
}
