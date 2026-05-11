package com.thesis.freelance.features.statistics.web;

import com.thesis.freelance.features.statistics.dto.StatisticsResponse;
import com.thesis.freelance.features.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> getPlatformStatistics(
            @RequestParam(required = false) Integer months,
            @RequestParam(required = false) Integer weeks
    ) {
        return ResponseEntity.ok(statisticsService.getPlatformStatistics(months, weeks));
    }
}
