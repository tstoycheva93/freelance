package com.thesis.freelance.features.review.service;

import com.thesis.freelance.common.exception.RevieweeNotFoundException;
import com.thesis.freelance.common.exception.UserNotFoundException;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.review.dto.CreateReviewDto;
import com.thesis.freelance.features.review.entity.Review;
import com.thesis.freelance.features.review.repository.ReviewRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public Review createReview(UUID reviewerId, CreateReviewDto dto) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new UserNotFoundException(reviewerId.toString()));

        User reviewee = userRepository.findById(dto.getRevieweeId())
                .orElseThrow(() -> new RevieweeNotFoundException());

        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .overallRating(dto.getOverallRating())
                .timelinessRating(dto.getTimelinessRating())
                .qualityRating(dto.getQualityRating())
                .content(dto.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        updateRevieweeRating(reviewee);

        return savedReview;
    }

    private void updateRevieweeRating(User reviewee) {
        List<Review> reviews = reviewRepository.findByRevieweeId(reviewee.getId());

        if (reviews.isEmpty()) {
            return;
        }

        double averageOverall = reviews.stream()
                .mapToInt(Review::getOverallRating)
                .average()
                .orElse(0.0);

        BigDecimal newRating = BigDecimal.valueOf(averageOverall)
                .setScale(2, RoundingMode.HALF_UP);

        long reviewCount = reviews.size();

        reviewee.setRating(newRating);
        reviewee.setReviewsCount(reviewCount);
        userRepository.save(reviewee);
    }
}
