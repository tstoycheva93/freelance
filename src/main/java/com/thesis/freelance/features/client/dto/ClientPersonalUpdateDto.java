package com.thesis.freelance.features.client.dto;

public record ClientPersonalUpdateDto(
        String occupation,
        String companyName,
        String bio,
        String website
) {
}
