package com.thesis.freelance.features.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thesis.freelance.common.util.ValidPassword;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AdminRegistrationRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 2, max = 100) String surname,
        @NotBlank @Size(min = 3, max = 50) @Pattern(regexp = "^[a-zA-Z0-9]+$") String username,
        @NotBlank @Size(min = 8, max = 100) @ValidPassword(min = 8, max = 100) String password,
        @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate birthDate
) {
}
