package com.thesis.freelance.features.review.repository;

import com.thesis.freelance.features.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByRevieweeId(UUID revieweeId);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.reviewee.id = :userId")
    Double getAverageRatingByUserId(@Param("userId") UUID userId);
}