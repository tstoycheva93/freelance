package com.thesis.freelance.features.catalog.dto;

import java.util.UUID;

public record CatalogSkillDto(UUID id, String name, String categoryName, boolean inUse) {
}
