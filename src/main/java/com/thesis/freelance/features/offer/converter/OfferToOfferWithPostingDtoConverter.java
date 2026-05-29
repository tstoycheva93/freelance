package com.thesis.freelance.features.offer.converter;

import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.offer.dto.OfferWithPostingDto;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.user.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class OfferToOfferWithPostingDtoConverter {

    public OfferWithPostingDto convertForFreelancer(Offer offer, User clientUser, String categoryName) {
        return OfferWithPostingDto.builder()
                .id(offer.getId())
                .price(offer.getPrice())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .deliveryTime(offer.getDeliveryTime())
                .submittedAt(offer.getCreatedAt())
                .postingId(offer.getPosting().getId())
                .postingTitle(offer.getPosting().getTitle())
                .postingPhotoUrl(offer.getPosting().getPhotoUrl())
                .postingBudget(offer.getPosting().getBudget())
                .postingDeadline(offer.getPosting().getDeadline())
                .postingStatus(offer.getPosting().getStatus())
                .categoryName(categoryName)
                .clientUserId(clientUser.getId())
                .clientPhotoUrl(clientUser.getPhotoUrl())
                .clientRating(clientUser.getRating())
                .clientReviewsCount(clientUser.getReviewsCount())
                .clientFirstName(clientUser.getName())
                .clientLastName(clientUser.getSurname())
                .build();
    }

    public OfferWithPostingDto convertForClient(Offer offer, Freelancer freelancer, User freelancerUser, String categoryName) {
        return OfferWithPostingDto.builder()
                .id(offer.getId())
                .price(offer.getPrice())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .deliveryTime(offer.getDeliveryTime())
                .submittedAt(offer.getCreatedAt())
                .postingId(offer.getPosting().getId())
                .postingTitle(offer.getPosting().getTitle())
                .postingPhotoUrl(offer.getPosting().getPhotoUrl())
                .postingBudget(offer.getPosting().getBudget())
                .postingDeadline(offer.getPosting().getDeadline())
                .postingStatus(offer.getPosting().getStatus())
                .categoryName(categoryName)
                .freelancerUserId(freelancerUser.getId())
                .freelancerPhotoUrl(freelancerUser.getPhotoUrl())
                .freelancerRating(freelancerUser.getRating())
                .freelancerReviewsCount(freelancerUser.getReviewsCount())
                .freelancerFirstName(freelancerUser.getName())
                .freelancerLastName(freelancerUser.getSurname())
                .experienceLevel(freelancer.getExperience())
                .build();
    }
}
