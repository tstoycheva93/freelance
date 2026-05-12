package com.thesis.freelance.features.flag.dto;

import java.util.List;

public record AdminFlagListResponse(
    List<AdminFlagDto> flags,
    int totalFiltered,
    int currentPage,
    int totalPages,
    int itemsPerPage,
    long totalFlags,
    long pendingCount,
    long approvedCount,
    long declinedCount
) {}
