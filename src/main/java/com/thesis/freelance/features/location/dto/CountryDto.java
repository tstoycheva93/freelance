package com.thesis.freelance.features.location.dto;

import java.util.UUID;

public record CountryDto(
    UUID id,
    String name,
    String initials
) {}
