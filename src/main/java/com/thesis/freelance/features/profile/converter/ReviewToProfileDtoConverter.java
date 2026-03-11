package com.thesis.freelance.features.profile.converter;


import com.thesis.freelance.features.review.dto.ReviewProfileDto;
import com.thesis.freelance.features.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewToProfileDtoConverter {

    public ReviewProfileDto mapReviewToDto(Review review) {
        return ReviewProfileDto.builder()
                .id(review.getId())
                .reviewerName(review.getReviewer().getFullName())
                .reviewerPhoto(review.getReviewer().getPhotoUrl())
                .createdAt(review.getCreatedAt())
                .rating(review.getOverallRating() != null ? review.getOverallRating().doubleValue() : null)
                .timeliness(review.getTimelinessRating() != null ? review.getTimelinessRating().doubleValue() : null)
                .quality(review.getQualityRating() != null ? review.getQualityRating().doubleValue() : null)
                .comment(review.getContent())
                .build();
    }


}
