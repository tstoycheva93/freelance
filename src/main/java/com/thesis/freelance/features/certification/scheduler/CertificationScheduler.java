package com.thesis.freelance.features.certification.scheduler;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.certification.entity.Certification;
import com.thesis.freelance.features.certification.repository.CertificationRepository;
import com.thesis.freelance.features.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificationScheduler {

    private final CertificationRepository certificationRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void checkExpiredCertifications() {
        log.info("Running scheduled task: checkExpiredCertifications");

        List<Certification> expiredCerts = certificationRepository
                .findExpiredActiveCertifications(ActivityStatus.ACTIVE, LocalDate.now());

        for (Certification cert : expiredCerts) {
            var userId = cert.getFreelancer().getUserId();

              if (userId != null) {
                log.info("Sending expired certificate notification to user: {}", userId);
                notificationService.notifyExpiredCertificate(userId, cert.getTitle());
                log.info("Notification sent successfully to user: {}", userId);
            } else {
                log.warn("Cannot send notification - userId is null for certification: {}", cert.getId());
            }
        }


        log.info("Expired certification check completed. Processed {} certifications", expiredCerts.size());
    }
}
