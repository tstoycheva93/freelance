package com.thesis.freelance.features.category.service;

import com.thesis.freelance.features.category.dto.CategoryDto;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllActive()
                .stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription(), c.getIconUrl()))
                .toList();
    }
}