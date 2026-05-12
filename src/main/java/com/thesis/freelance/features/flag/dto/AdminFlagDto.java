package com.thesis.freelance.features.flag.dto;

import java.util.UUID;

public record AdminFlagDto(
    UUID id,
    String targetType,
    String targetDisplay,
    String flagType,
    String reporterName,
    String createdAt,
    String status
) {}
