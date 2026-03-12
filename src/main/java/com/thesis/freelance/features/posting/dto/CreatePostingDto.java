package com.thesis.freelance.features.posting.dto;

import com.thesis.freelance.common.enums.ExperienceLevel;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.location.dto.LocationDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreatePostingDto(
        @NotNull(message = "Cannot be empty.") String title,
        @NotNull UUID categoryId,
        @NotNull(message = "Must upload photo.") String photoUrl,
        @NotNull(message = "Must set budget.") Integer budget,
        @NotNull(message = "Cannot be empty.") @Future(message = "Must be upcoming.") LocalDate deadline,
        @NotNull(message = "Cannot be empty.") String description,
        @NotNull(message = "Cannot be empty.") PostingStatus status,
        @NotNull(message = "Cannot be empty.") LocationDto location,
        @NotNull(message = "Cannot be empty.") ExperienceLevel experienceLevel,
        List<String> attachmentUrls,
        List<UUID> skillIds) implements Serializable {
}
