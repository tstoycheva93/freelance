package com.thesis.freelance.features.offer.converter;

import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.offer.dto.OfferForPostingDto;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class OfferToOfferForPostingDtoConverter {

    public OfferForPostingDto convert(Offer offer, Freelancer freelancer, User freelancerUser) {
        return OfferForPostingDto.builder()
                .id(offer.getId())
                .price(offer.getPrice())
                .message(offer.getMessage())
                .status(offer.getStatus())
                .deliveryTime(offer.getDeliveryTime())
                .submittedAt(offer.getCreatedAt())
                .postingTitle(offer.getPosting().getTitle())
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
