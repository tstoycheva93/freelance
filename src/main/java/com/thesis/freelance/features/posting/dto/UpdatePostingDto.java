package com.thesis.freelance.features.posting.dto;

import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.location.dto.LocationDto;
import jakarta.validation.constraints.Future;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdatePostingDto(
        String title,
        UUID categoryId,
        String photoUrl,
        Integer budget,
        LocalDate deadline,
        String description,
        PostingStatus status,
        LocationDto location,
        ExperienceLevel experienceLevel,
        List<String> attachmentUrls,
        List<UUID> skillIds) implements Serializable {
}
