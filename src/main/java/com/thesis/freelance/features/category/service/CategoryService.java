package com.thesis.freelance.features.category.service;

import com.thesis.freelance.features.category.dto.CategoryDto;
import com.thesis.freelance.features.category.dto.CategoryWithSkillsDto;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.skill.dto.SkillDto;
import com.thesis.freelance.features.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }

    public List<CategoryWithSkillsDto> getSkillCategoriesWithSkills() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    List<SkillDto> skills = skillRepository.findByCategory_Id(category.getId())
                            .stream()
                            .map(skill -> new SkillDto(
                                    skill.getId(),
                                    skill.getSkill(),
                                    category.getId(),
                                    category.getName()
                            ))
                            .collect(Collectors.toList());
                    return new CategoryWithSkillsDto(category.getId(), category.getName(), skills);
                })
                .collect(Collectors.toList());
    }

}
