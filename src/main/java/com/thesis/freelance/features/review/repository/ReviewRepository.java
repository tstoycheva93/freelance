package com.thesis.freelance.features.review.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE r.reviewee.id = :revieweeId AND r.activityStatus = :activityStatus ORDER BY r.createdAt DESC")
    List<Review> findByRevieweeId(@Param("revieweeId") UUID revieweeId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT r FROM Review r WHERE r.reviewee.id = :revieweeId AND r.activityStatus = :activityStatus ORDER BY r.createdAt DESC")
    Page<Review> findByRevieweeId(@Param("revieweeId") UUID revieweeId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.reviewer.id = :userId OR r.reviewee.id = :userId")
    List<Review> findAllByUserIdNoFilter(@Param("userId") UUID userId);
}