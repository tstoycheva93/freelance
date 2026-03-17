package com.thesis.freelance.features.freelancer.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record FreelancerCertificationDto(
        UUID id,
        @NotNull @Size(min = 2, max = 100) String title,
        @NotNull @Size(min = 2, max = 100) String organization,
        @Past @NotNull LocalDate date,
        @Future LocalDate expiration,
        @Size(max = 100) String identifier
) {
}
