package com.thesis.freelance.features.user.dto;

import java.util.List;

public record AdminUserListResponse(
    List<AdminUserDto> users,
    int totalUsers,
    int currentPage,
    int totalPages,
    int itemsPerPage,
    long totalActiveUsers,
    long newThisMonth,
    long verifiedUsers
) {}
