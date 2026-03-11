package com.thesis.freelance.features.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewProfileDto {
    private UUID id;
    private String reviewerName;
    private String reviewerPhoto;
    private LocalDateTime createdAt;
    private Double rating;
    private Double timeliness;
    private Double quality;
    private String comment;
}
