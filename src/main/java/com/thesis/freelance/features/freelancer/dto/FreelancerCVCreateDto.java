package com.thesis.freelance.features.freelancer.dto;

public record FreelancerCVCreateDto(
        String attachmentUrl,
        String attachmentName,
        Long sizeBytes
) {
}
