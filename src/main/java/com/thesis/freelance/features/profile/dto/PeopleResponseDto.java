package com.thesis.freelance.features.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeopleResponseDto {
    private List<PeopleDto> content;
    private boolean hasNext;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
