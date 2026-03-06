package com.thesis.freelance.features.user.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role,
        String phoneNumber,
        LocalDate dateOfBirth,
        String location,
        String profilePhoto
) {}