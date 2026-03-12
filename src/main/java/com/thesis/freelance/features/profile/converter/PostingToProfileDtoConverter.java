package com.thesis.freelance.features.profile.converter;

import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.posting.dto.PostingProfileDto;
import com.thesis.freelance.features.posting.entity.Posting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostingToProfileDtoConverter {
    private final CategoryRepository categoryRepository;

    public PostingProfileDto mapPostingToDto(Posting posting) {
        String categoryName = posting.getCategoryId() != null
                ? categoryRepository.findById(posting.getCategoryId())
                .map(Category::getName)
                .orElse(null)
                : null;

        String type = posting.getStatus() != null ? posting.getStatus().name() : null;

        String budget = posting.getBudget() != null ? "$" + posting.getBudget() : null;

        String views = posting.getViewsCount() != null ? formatViews(posting.getViewsCount()) : "0";

        return PostingProfileDto.builder()
                .id(posting.getId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .category(categoryName)
                .type(type)
                .budget(budget)
                .views(views)
                .photoUrl(posting.getPhotoUrl())
                .location(posting.getLocation() != null ? posting.getLocation().getName() : null)
                .deadline(posting.getDeadline())
                .build();
    }

    private String formatViews(Long views) {
        if (views >= 1000) {
            return String.format("%.1fk", views / 1000.0);
        }
        return views.toString();
    }

}
