package com.thesis.freelance.features.offer.dto;

import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.skill.dto.SkillDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferForPostingDto {
    private UUID id;
    private BigDecimal price;
    private String message;
    private OfferStatus status;
    private LocalDate deliveryTime;
    private LocalDateTime submittedAt;

    // Posting info
    private String postingTitle;

    // Freelancer info
    private UUID freelancerUserId;
    private String freelancerPhotoUrl;
    private BigDecimal freelancerRating;
    private Long freelancerReviewsCount;
    private String freelancerFirstName;
    private String freelancerLastName;
    private ExperienceLevel experienceLevel;
    private List<SkillDto> skills;
}
