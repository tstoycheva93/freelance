package com.thesis.freelance.features.review.web;

import com.thesis.freelance.features.review.dto.CreateReviewDto;
import com.thesis.freelance.features.review.entity.Review;
import com.thesis.freelance.features.review.service.ReviewService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/new-review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @Valid @RequestBody CreateReviewDto dto,
            Authentication authentication) {
        
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Review review = reviewService.createReview(currentUser.getId(), dto);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            Authentication authentication) {
        
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        reviewService.deleteReview(reviewId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
