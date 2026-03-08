package com.thesis.freelance.features.user.dto;

public record UserNotificationSettingsDto(
        Boolean emailsOn,
        Boolean readOn,
        Boolean pushOn
) {
}
