package com.thesis.freelance.features.freelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record FreelancerEducationDto(
        UUID id,
        @NotBlank @Size(min = 2, max = 100) String institution,
        @NotBlank @Size(min = 2, max = 100) String degree,
        @Size(min = 2, max = 100) String field,
        @Past @NotNull LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        @Size(max = 500) String description
) {
}
