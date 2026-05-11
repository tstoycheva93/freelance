package com.thesis.freelance.features.catalog.dto;

import java.util.UUID;

public record CatalogCategoryDto(UUID id, String name, boolean inUse) {
}
