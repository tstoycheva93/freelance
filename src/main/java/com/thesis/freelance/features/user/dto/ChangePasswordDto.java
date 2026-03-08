package com.thesis.freelance.features.user.dto;

import com.thesis.freelance.common.util.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100)
        @ValidPassword(min = 8, max = 100)
        String oldPassword,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100)
        @ValidPassword(min = 8, max = 100)
        String newPassword,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100)
        @ValidPassword(min = 8, max = 100)
        String confirmPassword
) {
}
