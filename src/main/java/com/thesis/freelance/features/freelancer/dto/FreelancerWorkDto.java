package com.thesis.freelance.features.freelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record FreelancerWorkDto(
        UUID id,
        @NotBlank @Size(min = 2, max = 100) String title,
        @NotBlank @Size(min = 2, max = 100) String company,
        @NotBlank String type,
        @Past @NotNull LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        @Size(max = 500) String description
) {
}
