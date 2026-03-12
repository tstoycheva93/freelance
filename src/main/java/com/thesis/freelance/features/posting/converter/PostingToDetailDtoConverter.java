package com.thesis.freelance.features.posting.converter;

import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.location.repository.LocationRepository;
import com.thesis.freelance.features.posting.dto.ClientFullDto;
import com.thesis.freelance.features.posting.dto.PostingDetailDto;
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
public class PostingToDetailDtoConverter {
    
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PostingSkillsRepository postingSkillsRepository;
    private final UserRepository userRepository;
    
    public PostingDetailDto convert(Posting posting) {
        String categoryName = getCategoryName(posting.getCategoryId());
        UUID locationId = getLocationId(posting.getLocation());
        String locationName = getLocationName(posting.getLocation());
        List<String> skills = postingSkillsRepository.findSkillNamesByPostingId(posting.getId());
        List<String> skillIds = postingSkillsRepository.findSkillIdsByPostingId(posting.getId());
        ClientFullDto clientDto = getClientFullDto(posting.getClient());
        
        return PostingDetailDto.builder()
                .id(posting.getId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .categoryId(posting.getCategoryId())
                .category(categoryName)
                .status(posting.getStatus())
                .budget(posting.getBudget())
                .photoUrl(posting.getPhotoUrl())
                .viewsCount(posting.getViewsCount())
                .createdAt(posting.getCreatedAt())
                .deadline(posting.getDeadline())
                .experienceLevel(posting.getExperienceLevel())
                .locationId(locationId)
                .locationName(locationName)
                .skills(skills)
                .skillIds(skillIds)
                .attachmentUrls(posting.getAttachmentUrls())
                .client(clientDto)
                .build();
    }
    
    private String getCategoryName(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse(null);
    }
    
    private UUID getLocationId(Location location) {
        if (location == null) return null;
        return location.getId();
    }
    
    private String getLocationName(Location location) {
        if (location == null) return null;
        return location.getName();
    }
    
    private ClientFullDto getClientFullDto(Client client) {
        if (client == null) return null;
        
        User user = userRepository.findById(client.getUserId()).orElse(null);
        if (user == null) return null;
        
        return ClientFullDto.builder()
                .id(client.getId())
                .name(user.getFullName())
                .role(client.getOccupation())
                .companyName(client.getCompanyName())
                .photoUrl(user.getPhotoUrl())
                .rating(user.getRating())
                .reviewsCount(user.getReviewsCount())
                .isVerified(user.getIsVerified())
                .bio(client.getBio())
                .website(client.getWebsite())
                .postingsCount(client.getPostingsCount())
                .build();
    }
}
