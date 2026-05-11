package com.thesis.freelance.features.user.dto;

import java.util.List;

public record AdminUserDetailResponse(
    AdminUserDto user,
    long postedJobs,
    long submitted,
    long completed,
    double rating,
    long reviewCount,
    List<FlagHistoryItem> recentFlags
) {

    public record FlagHistoryItem(String reason, String date, String status) {}
}
