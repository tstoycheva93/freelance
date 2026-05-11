package com.thesis.freelance.features.statistics.service;

import com.thesis.freelance.features.statistics.dto.DashboardResponse;
import com.thesis.freelance.features.statistics.dto.DashboardResponse.*;
import com.thesis.freelance.features.statistics.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepo;

    private static final int GROWTH_DAYS = 100;

    public DashboardResponse getDashboardData() {
        KPISegment kpis = buildKpis();
        GrowthChartSegment growthChart = buildGrowthChart();
        AttentionPanelSegment attentionPanel = buildAttentionPanel();

        return DashboardResponse.builder()
                .kpis(kpis)
                .growthChart(growthChart)
                .attentionPanel(attentionPanel)
                .build();
    }

    private KPISegment buildKpis() {
        return KPISegment.builder()
                .totalUsers(dashboardRepo.countTotalActiveUsers())
                .activePostings(dashboardRepo.countActivePostings())
                .completedOffers(dashboardRepo.countCompletedOffers())
                .newRegistrationsThisWeek(dashboardRepo.countNewRegistrationsThisWeek())
                .build();
    }

    private GrowthChartSegment buildGrowthChart() {
        List<Object[]> rows = dashboardRepo.findDailyUserGrowth(GROWTH_DAYS);

        Map<String, Long> dayCounts = new LinkedHashMap<>();
        for (Object[] r : rows) {
            dayCounts.put((String) r[0], toLong(r[1]));
        }

        List<DailyPoint> data = dayCounts.entrySet().stream()
                .map(e -> DailyPoint.builder()
                        .name(e.getKey())
                        .value(e.getValue())
                        .build())
                .collect(Collectors.toList());

        return GrowthChartSegment.builder().data(data).build();
    }

    private AttentionPanelSegment buildAttentionPanel() {
        long pendingFlags = dashboardRepo.countPendingFlags();
        long usersAwaitingUnlock = dashboardRepo.countInactiveUsers();
        Object[] maint = dashboardRepo.findMaintenanceConfig();

        boolean maintenanceMode = false;
        String maintenanceMessage = "";
        if (maint[0] != null) {
            if (maint[0] instanceof Boolean) {
                maintenanceMode = (Boolean) maint[0];
            } else if (maint[0] instanceof Number) {
                maintenanceMode = ((Number) maint[0]).intValue() == 1;
            }
        }
        if (maint[1] != null) {
            maintenanceMessage = (String) maint[1];
        }

        return AttentionPanelSegment.builder()
                .pendingFlags(pendingFlags)
                .usersAwaitingUnlock(usersAwaitingUnlock)
                .maintenanceMode(maintenanceMode)
                .maintenanceMessage(maintenanceMessage)
                .build();
    }

    private static long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }
}
