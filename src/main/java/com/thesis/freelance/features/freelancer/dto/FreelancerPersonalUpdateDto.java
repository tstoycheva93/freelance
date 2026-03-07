package com.thesis.freelance.features.freelancer.dto;

import com.thesis.freelance.common.enums.Availability;
import com.thesis.freelance.common.enums.ExperienceLevel;

public record FreelancerPersonalUpdateDto(
        String title,
        String bio,
        String website,
        ExperienceLevel experience,
        Availability availability
) {
}
