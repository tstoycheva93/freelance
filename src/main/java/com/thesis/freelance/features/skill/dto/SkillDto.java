package com.thesis.freelance.features.skill.dto;

import java.util.UUID;

public record SkillDto(UUID id, String skill, UUID categoryId, String categoryName) {
}
