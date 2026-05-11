package com.thesis.freelance.features.user.dto;

import java.util.UUID;

public record AdminUserDto(
    UUID id,
    String name,
    String email,
    String username,
    String role,
    String status,
    String registered,
    String born,
    String location,
    String avatar,
    boolean twoFactorEnabled,
    boolean pushNotificationsEnabled,
    boolean emailsEnabled,
    boolean emailVerified
) {}
