package com.thesis.freelance.features.posting.dto;

import java.util.List;

public record AdminPostingListResponse(
    List<AdminPostingDto> postings,
    int totalFiltered,
    int currentPage,
    int totalPages,
    int itemsPerPage,
    long totalPostings,
    long newThisMonth,
    long activePostings
) {}
