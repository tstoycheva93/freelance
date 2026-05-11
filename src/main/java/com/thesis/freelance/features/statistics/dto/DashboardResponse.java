package com.thesis.freelance.features.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private KPISegment kpis;
    private GrowthChartSegment growthChart;
    private AttentionPanelSegment attentionPanel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KPISegment {
        private long totalUsers;
        private long activePostings;
        private long completedOffers;
        private long newRegistrationsThisWeek;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthChartSegment {
        private List<DailyPoint> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPoint {
        private String name;
        private long value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttentionPanelSegment {
        private long pendingFlags;
        private long usersAwaitingUnlock;
        private boolean maintenanceMode;
        private String maintenanceMessage;
    }
}
