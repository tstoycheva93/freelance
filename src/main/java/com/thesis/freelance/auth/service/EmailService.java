package com.thesis.freelance.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

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

    public void sendVerificationEmail(String to, String verificationLink) {
        try {
            Context context = new Context();
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("appUrl", appUrl);
            
            String html = templateEngine.process("email/verification", context);
            sendHtmlEmail(to, "Welcome to TalentFlow - Verify Your Email", html);
        } catch (Exception e) {
            log.error("Failed to load verification template: {}", e.getMessage());
            String fallback = "Verify your email: " + verificationLink;
            sendEmail(to, "Verify your email - TalentFlow", fallback);
        }
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            context.setVariable("appUrl", appUrl);
            
            String html = templateEngine.process("email/forgot-password", context);
            sendHtmlEmail(to, "Reset Your Password - TalentFlow", html);
        } catch (Exception e) {
            log.error("Failed to load password reset template: {}", e.getMessage());
            String fallback = "Reset your password: " + resetLink;
            sendEmail(to, "Password Reset - TalentFlow", fallback);
        }
    }

    public void sendCongratulationsEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("appUrl", appUrl);
            
            String html = templateEngine.process("email/congratulations", context);
            sendHtmlEmail(to, "Welcome aboard! Your email is verified", html);
        } catch (Exception e) {
            log.error("Failed to load congratulations template: {}", e.getMessage());
            String fallback = "Congratulations! Your email has been verified. Welcome to TalentFlow!";
            sendEmail(to, "Welcome aboard! Your email is verified", fallback);
        }
    }

    public void sendTwoFactorAuthEmail(String to, String code, String deviceName) {
        try {
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("deviceName", deviceName);
            context.setVariable("appUrl", appUrl);
            
            String html = templateEngine.process("email/two-factor-auth", context);
            sendHtmlEmail(to, "Your verification code - TalentFlow", html);
        } catch (Exception e) {
            log.error("Failed to load 2FA template: {}", e.getMessage());
            String fallback = "Your verification code is: " + code + " (expires in 5 minutes)";
            sendEmail(to, "Your verification code - TalentFlow", fallback);
        }
    }
}
