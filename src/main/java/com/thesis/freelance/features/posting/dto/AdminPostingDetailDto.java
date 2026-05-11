package com.thesis.freelance.features.posting.dto;

import java.util.List;
import java.util.UUID;

public record AdminPostingDetailDto(
    UUID id,
    String title,
    String description,
    String category,
    String status,
    String activityStatus,
    Integer budget,
    Long viewsCount,
    String photoUrl,
    String datePosted,
    String deadline,
    String experience,
    String location,
    List<String> skills,
    List<String> attachmentUrls,
    boolean isFlagged,
    String clientName,
    String clientUsername,
    UUID clientUserId,
    double clientRating,
    long clientReviewCount,
    List<AdminOfferDto> offers
) {}
