package com.thesis.freelance.features.posting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostingProfileDto {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String budget;
    private String views;
    private String imageUrl;
}
