package com.thesis.freelance.features.freelancer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FreelancerCVDto(
        UUID id,
        String attachmentName,
        String attachmentUrl,
        LocalDateTime uploadedAt
) {
}
