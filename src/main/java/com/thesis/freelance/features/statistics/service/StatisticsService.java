package com.thesis.freelance.features.statistics.service;

import com.thesis.freelance.features.statistics.dto.StatisticsResponse;
import com.thesis.freelance.features.statistics.dto.StatisticsResponse.*;
import com.thesis.freelance.features.statistics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepo;

    private static final int DEFAULT_MONTHS = 6;
    private static final int DEFAULT_WEEKS = 4;
    private static final int TOP_FREELANCERS_LIMIT = 3;

    public StatisticsResponse getPlatformStatistics(Integer months, Integer weeks) {
        int m = months != null ? months : DEFAULT_MONTHS;
        int w = weeks != null ? weeks : DEFAULT_WEEKS;

        UserGrowthSegment userGrowth = buildUserGrowth(m);
        OfferFunnelSegment offerFunnel = buildOfferFunnel();
        PostingActivitySegment postingActivity = buildPostingActivity(w);
        List<TopCategoryItem> topCategories = buildTopCategories();
        List<GeoDistributionItem> geoDistribution = buildGeoDistribution();
        List<TopFreelancerItem> topFreelancers = buildTopFreelancers();

        return StatisticsResponse.builder()
                .userGrowth(userGrowth)
                .offerFunnel(offerFunnel)
                .postingActivity(postingActivity)
                .topCategories(topCategories)
                .geoDistribution(geoDistribution)
                .topFreelancers(topFreelancers)
                .build();
    }

    private UserGrowthSegment buildUserGrowth(int months) {
        List<Object[]> rows = statisticsRepo.findUserGrowth(months);

        List<MonthlyPoint> data = rows.stream()
                .map(r -> MonthlyPoint.builder()
                        .month((String) r[0])
                        .freelancers(toLong(r[1]))
                        .clients(toLong(r[2]))
                        .build())
                .collect(Collectors.toList());

        return UserGrowthSegment.builder().data(data).build();
    }

    private OfferFunnelSegment buildOfferFunnel() {
        List<Object[]> rows = statisticsRepo.findOfferFunnel();
        if (rows.isEmpty()) {
            return new OfferFunnelSegment(0, 0, 0);
        }
        Object[] row = rows.get(0);
        return OfferFunnelSegment.builder()
                .submitted(toLong(row[0]))
                .accepted(toLong(row[1]))
                .completed(toLong(row[2]))
                .build();
    }

    private PostingActivitySegment buildPostingActivity(int weeks) {
        List<Object[]> createdRows = statisticsRepo.findPostingActivity(weeks);
        List<Object[]> completedRows = statisticsRepo.findCompletedOffersPerWeek(weeks);
        List<Object[]> closedRows = statisticsRepo.findClosedPostingsPerWeek(weeks);

        Map<Integer, Long> createdMap = toPeriodMap(createdRows);
        Map<Integer, Long> completedMap = toPeriodMap(completedRows);
        Map<Integer, Long> closedMap = toPeriodMap(closedRows);

        Set<Integer> allPeriods = new TreeSet<>();
        allPeriods.addAll(createdMap.keySet());
        allPeriods.addAll(completedMap.keySet());
        allPeriods.addAll(closedMap.keySet());

        if (allPeriods.isEmpty()) {
            return PostingActivitySegment.builder().data(List.of()).build();
        }

        int weekNum = 1;
        List<WeeklyPoint> data = new ArrayList<>();
        for (Integer period : allPeriods) {
            data.add(WeeklyPoint.builder()
                    .week("W" + weekNum++)
                    .created(createdMap.getOrDefault(period, 0L))
                    .completed(completedMap.getOrDefault(period, 0L))
                    .closed(closedMap.getOrDefault(period, 0L))
                    .build());
        }

        return PostingActivitySegment.builder().data(data).build();
    }

    private List<TopCategoryItem> buildTopCategories() {
        List<Object[]> rows = statisticsRepo.findTopCategories();

        return rows.stream()
                .map(r -> TopCategoryItem.builder()
                        .name((String) r[0])
                        .count(toLong(r[1]))
                        .build())
                .collect(Collectors.toList());
    }

    private List<GeoDistributionItem> buildGeoDistribution() {
        List<Object[]> rows = statisticsRepo.findGeoDistribution();

        long total = rows.stream().mapToLong(r -> toLong(r[1])).sum();
        if (total == 0) return List.of();

        List<GeoDistributionItem> items = new ArrayList<>();
        long accounted = 0;

        for (int i = 0; i < rows.size(); i++) {
            String country = (String) rows.get(i)[0];
            long count = toLong(rows.get(i)[1]);
            accounted += count;

            if (i < 4) {
                double pct = Math.round((double) count / total * 10000.0) / 100.0;
                items.add(GeoDistributionItem.builder()
                        .country(country)
                        .percentage(pct)
                        .build());
            }
        }

        long remaining = total - accounted;
        if (remaining > 0) {
            double pct = Math.round((double) remaining / total * 10000.0) / 100.0;
            items.add(GeoDistributionItem.builder()
                    .country("Others")
                    .percentage(pct)
                    .build());
        }

        return items;
    }

    private List<TopFreelancerItem> buildTopFreelancers() {
        List<Object[]> rows = statisticsRepo.findTopFreelancers(TOP_FREELANCERS_LIMIT);

        return rows.stream().map(r -> {
            String name = (String) r[0];
            String surname = (String) r[1];
            double rating = r[2] != null ? ((Number) r[2]).doubleValue() : 0.0;
            long projects = toLong(r[3]);
            String photoUrl = (String) r[4];
            String initials = (name != null ? name.substring(0, 1) : "")
                    + (surname != null ? surname.substring(0, 1) : "");
            String fullName = name + " " + surname;

            return TopFreelancerItem.builder()
                    .name(fullName)
                    .rating(rating)
                    .projects(projects)
                    .avatar(photoUrl != null ? photoUrl : "")
                    .initials(initials)
                    .build();
        }).collect(Collectors.toList());
    }

    private static long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private static Map<Integer, Long> toPeriodMap(List<Object[]> rows) {
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put(((Number) r[0]).intValue(), toLong(r[1]));
        }
        return map;
    }
}
