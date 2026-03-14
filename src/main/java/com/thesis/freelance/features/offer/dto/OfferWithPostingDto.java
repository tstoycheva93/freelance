package com.thesis.freelance.features.offer.dto;

import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.common.enums.PostingStatus;
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
public class OfferWithPostingDto {
    //  Offer
    private UUID id;
    private BigDecimal price;
    private String message;
    private OfferStatus status;
    private LocalDate deliveryTime;
    private LocalDateTime submittedAt;

    //  Posting
    private UUID postingId;
    private String postingTitle;
    private String postingPhotoUrl;
    private Integer postingBudget;
    private LocalDate postingDeadline;
    private PostingStatus postingStatus;
    private String categoryName;

    // (if freelancer viewing)
    private UUID clientUserId;
    private String clientPhotoUrl;
    private BigDecimal clientRating;
    private Long clientReviewsCount;
    private String clientFirstName;
    private String clientLastName;

    // (if client viewing)
    private UUID freelancerUserId;
    private String freelancerPhotoUrl;
    private BigDecimal freelancerRating;
    private Long freelancerReviewsCount;
    private String freelancerFirstName;
    private String freelancerLastName;
    private ExperienceLevel experienceLevel;
    private List<SkillDto> skills;
}
