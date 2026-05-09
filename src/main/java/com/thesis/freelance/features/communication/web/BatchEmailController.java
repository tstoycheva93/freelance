package com.thesis.freelance.features.communication.web;

import com.thesis.freelance.features.communication.dto.BatchEmailRequest;
import com.thesis.freelance.features.communication.dto.BatchNotificationRequest;
import com.thesis.freelance.features.communication.dto.SmtpStatusResponse;
import com.thesis.freelance.features.communication.service.BatchEmailService;
import com.thesis.freelance.features.communication.service.BatchNotificationService;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/office/communication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BatchEmailController {

    private final BatchEmailService batchEmailService;
    private final BatchNotificationService batchNotificationService;
    private final JavaMailSenderImpl mailSender;
    private final Environment environment;
    private final UserRepository userRepository;

    @PostMapping("/batch-email")
    public ResponseEntity<Map<String, Object>> sendBatchEmail(
            @Valid @RequestBody BatchEmailRequest request
    ) {
        batchEmailService.sendBatch(request);

        return ResponseEntity.ok(Map.of(
                "message", "Batch email queued for processing",
                "audience", request.audience().name(),
                "topic", request.topic()
        ));
    }

    @PostMapping("/batch-notification")
    public ResponseEntity<Map<String, Object>> sendBatchNotification(
            @Valid @RequestBody BatchNotificationRequest request
    ) {
        batchNotificationService.sendBatch(request);

        return ResponseEntity.ok(Map.of(
                "message", "Batch notifications queued for processing"
        ));
    }

    @GetMapping("/smtp-status")
    public ResponseEntity<SmtpStatusResponse> getSmtpStatus() {
        String host = environment.getProperty("spring.mail.host", "unknown");
        int port = Integer.parseInt(environment.getProperty("spring.mail.port", "587"));
        String username = environment.getProperty("spring.mail.username", "unknown");

        boolean connected;
        String message;
        try {
            mailSender.testConnection();
            connected = true;
            message = "SMTP server is reachable";
        } catch (Exception e) {
            connected = false;
            message = "SMTP connection failed: " + e.getMessage();
        }

        return ResponseEntity.ok(new SmtpStatusResponse(username, host, port, connected, message));
    }

    @PostMapping("/test-email")
    public ResponseEntity<Map<String, String>> sendTestEmail(Authentication authentication) {
        var user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var mimeMessage = mailSender.createMimeMessage();
        try {
            var helper = new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Test Email from TalentFlow");
            helper.setText("This is a test email sent from the TalentFlow admin panel to verify SMTP configuration.", true);
            helper.setFrom(environment.getProperty("spring.mail.username", "no-reply@example.com"));
            mailSender.send(mimeMessage);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Failed to send test email", e);
        }

        return ResponseEntity.ok(Map.of(
                "message", "Test email sent successfully",
                "recipient", user.getEmail()
        ));
    }
}
