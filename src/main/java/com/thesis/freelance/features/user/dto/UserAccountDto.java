package com.thesis.freelance.features.user.dto;

import com.thesis.freelance.features.location.dto.LocationDto;

import java.time.LocalDate;
import java.util.UUID;

public record UserAccountDto(
        UUID id,
        String name,
        String surname,
        String username,
        String email,
        String phone,
        LocationDto location,
        String photoUrl,
        LocalDate birthDate,
        Boolean isVerified,
        Boolean isPublic
) {
}
