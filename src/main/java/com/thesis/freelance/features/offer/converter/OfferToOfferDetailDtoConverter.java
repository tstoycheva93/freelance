package com.thesis.freelance.features.offer.converter;

import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.freelancer.dto.FreelancerCVDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.offer.dto.OfferDetailDto;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import com.thesis.freelance.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferToOfferDetailDtoConverter {

    private final CategoryRepository categoryRepository;

    public OfferDetailDto convert(Offer offer, Posting posting, Freelancer freelancer, User freelancerUser, UploadedCV cv) {
        Category category = posting.getCategoryId() != null
                ? categoryRepository.findById(posting.getCategoryId()).orElse(null)
                : null;

        FreelancerCVDto cvDto = cv != null
                ? new FreelancerCVDto(cv.getId(), cv.getAttachmentName(), cv.getAttachmentUrl(), cv.getUploadedAt())
                : null;

        return new OfferDetailDto(
                offer.getId(),
                offer.getStatus(),
                offer.getPrice(),
                offer.getMessage(),
                offer.getDeliveryTime(),
                offer.getAttachments(),
                offer.getDeliverableUrls(),
                offer.isDeliverableVisible(),
                offer.getDeliveredAt(),
                offer.getCreatedAt(),

                posting.getId(),
                posting.getStatus(),
                posting.getTitle(),
                category,
                posting.getPhotoUrl(),
                posting.getDescription(),
                posting.getDeadline(),
                posting.getBudget(),
                posting.getClient().getUserId(),

                freelancer.getId(),
                freelancer.getUserId(),
                freelancer.getTitle(),
                freelancer.getBio(),
                freelancer.getExperience(),
                freelancer.getAvailability(),
                freelancer.getWebsite(),
                cvDto,

                freelancerUser != null ? freelancerUser.getRating() : null,
                freelancerUser != null ? freelancerUser.getReviewsCount() : null,
                freelancerUser != null ? freelancerUser.getEmail() : null,
                freelancerUser != null && freelancerUser.getLocation() != null ? freelancerUser.getLocation().getName() : null,
                freelancerUser != null ? freelancerUser.getPhotoUrl() : null,
                freelancerUser != null ? freelancerUser.getIsVerified() : null,
                freelancerUser != null ? freelancerUser.getName() : null,
                freelancerUser != null ? freelancerUser.getSurname() : null,
                freelancerUser != null ? freelancerUser.getPhone() : null
        );
    }
}
