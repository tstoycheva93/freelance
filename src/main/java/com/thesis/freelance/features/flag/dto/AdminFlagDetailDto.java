package com.thesis.freelance.features.flag.dto;

import java.util.UUID;

public record AdminFlagDetailDto(
    UUID id,
    String targetType,
    UUID targetId,
    String targetDisplay,
    String flagType,
    String status,
    String reporterName,
    UUID reporterUserId,
    String createdAt,
    String reviewerName,
    String reviewReason,
    String reviewTime,
    String reviewContent
) {}
