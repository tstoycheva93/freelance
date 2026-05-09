package com.thesis.freelance.features.communication.service;

import com.thesis.freelance.common.enums.NotificationType;
import com.thesis.freelance.features.communication.dto.BatchNotificationRequest;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchNotificationService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Async("batchEmailExecutor")
    public void sendBatch(BatchNotificationRequest request) {
        List<User> recipients = userRepository.findByEmailsOnTrue();

        if (recipients.isEmpty()) {
            log.warn("Batch notification requested but no recipients found");
            return;
        }

        int created = 0;
        int skipped = 0;

        for (User user : recipients) {
            if (user.isAdmin()) {
                skipped++;
                continue;
            }

            try {
                var dto = notificationService.createAndNotify(
                        user.getId(),
                        NotificationType.CUSTOM,
                        request.title(),
                        request.body(),
                        request.deepLink() != null ? request.deepLink() : "/office"
                );

                if (dto != null) {
                    created++;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                log.error("Failed to create notification for user {}: {}", user.getEmail(), e.getMessage());
                skipped++;
            }
        }

        log.info("Batch notification completed. Created: {}, Skipped: {}", created, skipped);
    }
}
