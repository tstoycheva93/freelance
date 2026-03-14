package com.thesis.freelance.features.offer.converter;

import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.skill.dto.SkillDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class SkillListMapper implements Function<List<FreelancerSkills>, List<SkillDto>> {

    @Override
    public List<SkillDto> apply(List<FreelancerSkills> freelancerSkills) {
        if (freelancerSkills == null) {
            return List.of();
        }
        
        return freelancerSkills.stream()
                .map(fs -> new SkillDto(
                        fs.getSkill().getId(),
                        fs.getSkill().getSkill(),
                        fs.getSkill().getCategory().getId(),
                        fs.getSkill().getCategory().getName()
                ))
                .toList();
    }
}
