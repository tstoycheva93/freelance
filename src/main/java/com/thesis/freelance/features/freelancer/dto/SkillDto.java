package com.thesis.freelance.features.freelancer.dto;

import java.util.UUID;

public record SkillDto(UUID id, String skill, UUID categoryId, String categoryName) {
}
