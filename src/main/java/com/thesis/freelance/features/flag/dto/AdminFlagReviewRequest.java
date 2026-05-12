package com.thesis.freelance.features.flag.dto;

import com.thesis.freelance.common.enums.FlagStatus;
import jakarta.validation.constraints.NotNull;

public record AdminFlagReviewRequest(
    @NotNull FlagStatus status,
    String reason
) {}
