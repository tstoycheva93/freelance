package com.thesis.freelance.features.communication.service;

import com.thesis.freelance.auth.service.EmailService;
import com.thesis.freelance.features.communication.dto.BatchEmailRequest;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEmailService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Async("batchEmailExecutor")
    public void sendBatch(BatchEmailRequest request) {
        List<User> recipients = resolveAudience(request.audience());

        if (recipients.isEmpty()) {
            log.warn("Batch email requested but no recipients found for audience: {}", request.audience());
            return;
        }

        log.info("Starting batch email for audience={} with {} recipients", request.audience(), recipients.size());

        AtomicInteger sent = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        for (User user : recipients) {
            try {
                String html = buildEmailContent(user, request);
                emailService.sendBatchEmail(user.getEmail(), request.subject(), html);
                sent.incrementAndGet();
            } catch (Exception e) {
                log.error("Failed to queue batch email for user {}: {}", user.getEmail(), e.getMessage());
                failed.incrementAndGet();
            }
        }

        log.info("Batch email completed. Sent: {}, Failed: {}", sent.get(), failed.get());
    }

    private List<User> resolveAudience(BatchEmailRequest.Audience audience) {
        return switch (audience) {
            case ALL_USERS -> userRepository.findByEmailsOnTrue()
                    .stream()
                    .filter(u -> !u.isAdmin())
                    .toList();
            case CLIENTS -> userRepository.findByUserRole_RoleAndEmailsOnTrue("CLIENT");
            case FREELANCERS -> userRepository.findByUserRole_RoleAndEmailsOnTrue("FREELANCER");
            case NEW_USERS -> userRepository.findRecentByEmailsOn(LocalDateTime.now().minusDays(14));
        };
    }

    private String buildEmailContent(User user, BatchEmailRequest request) {
        Context context = new Context();
        context.setVariable("firstName", user.getName());
        context.setVariable("subject", request.subject());
        context.setVariable("body", formatBody(request.body(), user.getName()));

        return templateEngine.process("email/batch", context);
    }

    private String formatBody(String rawBody, String firstName) {
        String resolved = rawBody.replace("{{firstName}}", firstName);
        return "<p>" + resolved.replace("\n", "</p><p>") + "</p>";
    }
}
