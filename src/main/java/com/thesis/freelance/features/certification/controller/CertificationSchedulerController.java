package com.thesis.freelance.features.certification.controller;

import com.thesis.freelance.features.certification.scheduler.CertificationScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cert/scheduler")
@RequiredArgsConstructor
@Slf4j
public class CertificationSchedulerController {

    private final CertificationScheduler certificationScheduler;

    @PostMapping("/certifications/check-expired")
    public ResponseEntity<String> checkExpiredCertifications() {
        log.info("Manual trigger: checkExpiredCertifications invoked");
        
        try {
            certificationScheduler.checkExpiredCertifications();
            return ResponseEntity.ok("Expired certification check completed. Check server logs for details.");
        } catch (Exception e) {
            log.error("Manual trigger failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Certification check failed: " + e.getMessage());
        }
    }
}
