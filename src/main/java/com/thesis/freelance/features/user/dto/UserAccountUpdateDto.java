package com.thesis.freelance.features.user.dto;

import com.thesis.freelance.common.util.ValidPhoneNumber;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record UserAccountUpdateDto(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 2, max = 100) String surname,
        @NotBlank @Email String email,
        @ValidPhoneNumber(message = "Invalid phone number. Use format: +[country code][number]") String phone,
        UUID locationId,
        String photoUrl,
        Boolean isPublic
) {
}
