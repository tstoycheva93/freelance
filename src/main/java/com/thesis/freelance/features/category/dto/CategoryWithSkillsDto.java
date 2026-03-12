package com.thesis.freelance.features.category.dto;

import com.thesis.freelance.features.skill.dto.SkillDto;

import java.util.List;
import java.util.UUID;

public record CategoryWithSkillsDto(UUID id, String name, List<SkillDto> skills) {
}
