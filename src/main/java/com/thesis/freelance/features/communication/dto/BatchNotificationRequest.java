package com.thesis.freelance.features.communication.dto;

import jakarta.validation.constraints.NotBlank;

public record BatchNotificationRequest(
        @NotBlank String title,
        @NotBlank String body,
        String deepLink
) {}
