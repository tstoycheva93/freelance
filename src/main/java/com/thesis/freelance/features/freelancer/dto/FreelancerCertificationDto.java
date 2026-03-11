package com.thesis.freelance.features.freelancer.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;

public record FreelancerCertificationDto(
        UUID id,
        String title,
        String organization,
        @Past LocalDate date,
        @Future LocalDate expiration,
        String identifier
) {
}
