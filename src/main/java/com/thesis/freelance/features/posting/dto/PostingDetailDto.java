package com.thesis.freelance.features.posting.dto;

import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.PostingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostingDetailDto {
    private UUID id;
    private String title;
    private String description;
    private UUID categoryId;
    private String category;
    private PostingStatus status;
    private Integer budget;
    private String photoUrl;
    private Long viewsCount;
    private LocalDateTime createdAt;
    private LocalDate deadline;
    private ExperienceLevel experienceLevel;
    private UUID locationId;
    private String locationName;
    private List<String> skills;
    private List<String> skillIds;
    private List<String> attachmentUrls;
    private ClientFullDto client;
}
