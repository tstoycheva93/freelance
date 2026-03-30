package com.thesis.freelance.features.offer.dto;

import com.thesis.freelance.common.enums.Availability;
import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.freelancer.dto.FreelancerCVDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OfferDetailDto(
    UUID id,
    OfferStatus status,
    BigDecimal price,
    String message,
    LocalDate deliveryTime,
    List<String> attachments,
    List<String> deliverableUrls,
    boolean deliverableVisible,
    LocalDateTime deliveredAt,
    LocalDateTime createdAt,
    
    UUID postingId,
    PostingStatus postingStatus,
    Boolean postingDeleted,
    String postingTitle,
    Category postingCategory,
    String postingPhotoUrl,
    String postingDescription,
    LocalDate postingDeadline,
    Integer postingBudget,
    UUID postingClientId,
    
    UUID freelancerId,
    UUID freelancerUserId,
    String freelancerTitle,
    String freelancerDescription,
    ExperienceLevel freelancerExperienceLevel,
    Availability freelancerAvailability,
    String freelancerWebsite,
    FreelancerCVDto freelancerCv,
    
    BigDecimal freelancerRating,
    Long freelancerReviewCount,
    String freelancerEmail,
    String freelancerLocation,
    String freelancerPhotoUrl,
    Boolean freelancerIsVerified,
    String freelancerFirstName,
    String freelancerLastName,
    String freelancerPhone
) {}
