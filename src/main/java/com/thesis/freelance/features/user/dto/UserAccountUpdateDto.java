package com.thesis.freelance.features.user.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record UserAccountUpdateDto(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 2, max = 100) String surname,
        @NotBlank @Email String email,
        @Pattern(regexp = "^\\+3598[7-9]\\d{7}$", message = "Phone number must be valid Bulgarian mobile (e.g., +35988xxxxxxx)") String phone,
        UUID locationId,
        String photoUrl,
        Boolean isPublic
) {
}
