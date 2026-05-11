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
public class StatisticsResponse {

    private UserGrowthSegment userGrowth;
    private OfferFunnelSegment offerFunnel;
    private PostingActivitySegment postingActivity;
    private List<TopCategoryItem> topCategories;
    private List<GeoDistributionItem> geoDistribution;
    private List<TopFreelancerItem> topFreelancers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGrowthSegment {
        private List<MonthlyPoint> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPoint {
        private String month;
        private long freelancers;
        private long clients;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfferFunnelSegment {
        private long submitted;
        private long accepted;
        private long completed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostingActivitySegment {
        private List<WeeklyPoint> data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyPoint {
        private String week;
        private long created;
        private long completed;
        private long closed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCategoryItem {
        private String name;
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoDistributionItem {
        private String country;
        private double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopFreelancerItem {
        private String name;
        private double rating;
        private long projects;
        private String avatar;
        private String initials;
    }
}
