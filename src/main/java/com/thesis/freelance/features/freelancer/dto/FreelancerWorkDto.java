package com.thesis.freelance.features.freelancer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;

public record FreelancerWorkDto(
        UUID id,
        String title,
        String company,
        String type,
        @Past @NotNull LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String description
) {
}
