package com.thesis.freelance.features.freelancer.dto;

import com.thesis.freelance.common.enums.Availability;
import com.thesis.freelance.common.enums.ExperienceLevel;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FreelancerDetailsDto(
        UUID id,
        String firstName,
        String lastName,
        String title,
        String bio,
        ExperienceLevel experience,
        Availability availability,
        String website,
        List<FreelancerSkillDto> skills,
        List<FreelancerEducationDto> education,
        List<FreelancerWorkDto> work,
        List<FreelancerCertificationDto> certifications,
        FreelancerCVDto cv
) {
}
