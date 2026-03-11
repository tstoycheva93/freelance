package com.thesis.freelance.features.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDto {

    @NotNull(message = "Reviewee ID is required")
    private UUID revieweeId;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Overall rating must be at least 1")
    @Max(value = 5, message = "Overall rating must be at most 5")
    private Integer overallRating;

    @NotNull(message = "Timeliness rating is required")
    @Min(value = 1, message = "Timeliness rating must be at least 1")
    @Max(value = 5, message = "Timeliness rating must be at most 5")
    private Integer timelinessRating;

    @NotNull(message = "Quality rating is required")
    @Min(value = 1, message = "Quality rating must be at least 1")
    @Max(value = 5, message = "Quality rating must be at most 5")
    private Integer qualityRating;

    @NotBlank(message = "Review content is required")
    private String content;
}
