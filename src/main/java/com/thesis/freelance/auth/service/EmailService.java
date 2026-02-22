package com.thesis.freelance.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("");

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
        }
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        String subject = "Verify your email - Freelance Platform";
        String body = String.format(
                "Welcome to Freelance Platform!\n\n" +
                "Please verify your email by clicking the link below:\n\n" +
                "%s\n\n" +
                "This link expires in 10 minutes.\n\n" +
                "If you didn't create an account, please ignore this email.",
                verificationLink
        );
        sendEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset - Freelance Platform";
        String body = String.format(
                "You requested a password reset.\n\n" +
                "Click the link below to reset your password:\n\n" +
                "%s\n\n" +
                "This link expires in 10 minutes.\n\n" +
                "If you didn't request a password reset, please ignore this email.",
                resetLink
        );
        sendEmail(to, subject, body);
    }
}
