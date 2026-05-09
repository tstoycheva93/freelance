package com.thesis.freelance.features.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BatchEmailRequest(
        @NotNull Audience audience,
        @NotBlank String topic,
        @NotBlank String subject,
        @NotBlank String body
) {
    public enum Audience {
        ALL_USERS,
        CLIENTS,
        FREELANCERS,
        NEW_USERS
    }
}
