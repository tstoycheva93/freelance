package com.thesis.freelance.features.posting.dto;

import java.util.UUID;

public record AdminPostingDto(
    UUID id,
    String title,
    String category,
    Integer budget,
    String datePosted,
    String experience,
    String location,
    String status,
    boolean isFlagged,
    String activityStatus
) {}
