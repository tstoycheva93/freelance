package com.thesis.freelance.features.posting.converter;

import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.location.repository.LocationRepository;
import com.thesis.freelance.features.posting.dto.ClientMiniDto;
import com.thesis.freelance.features.posting.dto.PostingTileDto;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.repository.PostingSkillsRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostingToTileDtoConverter {
    
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PostingSkillsRepository postingSkillsRepository;
    private final UserRepository userRepository;
    
    public PostingTileDto convert(Posting posting) {
        String categoryName = getCategoryName(posting.getCategoryId());
        String locationName = getLocationName(posting.getLocation());
        List<String> skills = postingSkillsRepository.findSkillNamesByPostingId(posting.getId());
        ClientMiniDto clientDto = getClientMiniDto(posting.getClient());
        
        return PostingTileDto.builder()
                .id(posting.getId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .category(categoryName)
                .status(posting.getStatus())
                .budget(posting.getBudget())
                .photoUrl(posting.getPhotoUrl())
                .viewsCount(posting.getViewsCount())
                .createdAt(posting.getCreatedAt())
                .deadline(posting.getDeadline())
                .experienceLevel(posting.getExperienceLevel())
                .locationName(locationName)
                .skills(skills)
                .client(clientDto)
                .build();
    }
    
    private String getCategoryName(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse(null);
    }
    
    private String getLocationName(Location location) {
        if (location == null) return null;
        return location.getName();
    }
    
    private ClientMiniDto getClientMiniDto(Client client) {
        if (client == null) return null;
        
        User user = userRepository.findById(client.getUserId()).orElse(null);
        if (user == null) return null;
        
        return ClientMiniDto.builder()
                .id(client.getId())
                .name(user.getFullName())
                .role(client.getOccupation())
                .companyName(client.getCompanyName())
                .photoUrl(user.getPhotoUrl())
                .rating(user.getRating())
                .reviewsCount(user.getReviewsCount())
                .isVerified(user.getIsVerified())
                .build();
    }
}
