package com.thesis.freelance.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;

    @Value("${app.base-url-front}")
    private String appUrl;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(fromEmail);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}. Error: {}", to, e.getMessage());
        }
    }

    private String loadTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email/" + templateName + ".html");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        try {
            String html = loadTemplate("verification")
                    .replace("{{verificationLink}}", verificationLink)
                    .replace("{{appUrl}}", appUrl);
            sendHtmlEmail(to, "Verify your email - Freelance Platform", html);
        } catch (IOException e) {
            log.error("Failed to load verification template: {}", e.getMessage());
            String fallback = "Verify your email: " + verificationLink;
            sendEmail(to, "Verify your email - Freelance Platform", fallback);
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            String html = loadTemplate("password-reset")
                    .replace("{{resetLink}}", resetLink)
                    .replace("{{appUrl}}", appUrl);
            sendHtmlEmail(to, "Password Reset - Freelance Platform", html);
        } catch (IOException e) {
            log.error("Failed to load password reset template: {}", e.getMessage());
            String fallback = "Reset your password: " + resetLink;
            sendEmail(to, "Password Reset - Freelance Platform", fallback);
        }
    }

    public void sendCongratulationsEmail(String to) {
        try {
            String html = loadTemplate("congratulations")
                    .replace("{{appUrl}}", appUrl);
            sendHtmlEmail(to, "Welcome aboard! Your email is verified", html);
        } catch (IOException e) {
            log.error("Failed to load congratulations template: {}", e.getMessage());
            String fallback = "Congratulations! Your email has been verified. Welcome to Freelance Platform!";
            sendEmail(to, "Welcome aboard! Your email is verified", fallback);
        }
    }
}
