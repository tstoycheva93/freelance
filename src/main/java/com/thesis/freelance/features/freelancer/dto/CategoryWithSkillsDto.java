package com.thesis.freelance.features.freelancer.dto;

import java.util.List;
import java.util.UUID;

public record CategoryWithSkillsDto(UUID id, String name, List<SkillDto> skills) {
}
