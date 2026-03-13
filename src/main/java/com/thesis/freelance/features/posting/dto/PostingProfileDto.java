package com.thesis.freelance.features.posting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String photoUrl;
    private LocalDate deadline;
    private String location;
    private ClientDto client;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientDto {
        private UUID id;
        private String name;
        private String photoUrl;
    }
}
