package com.thesis.freelance.features.category.dto;

import java.util.UUID;

public record CategoryDto(UUID id, String name, String description, String iconUrl) {
}
