package com.thesis.freelance.features.user.dto;
import com.thesis.freelance.features.location.dto.LocationDto;
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
        LocationDto location,
        String profilePhoto,
        UUID freelancerId,
        UUID clientId
) {}