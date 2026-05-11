package com.thesis.freelance.features.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateSkillRequest(@NotBlank String name, @NotNull UUID categoryId) {
}
