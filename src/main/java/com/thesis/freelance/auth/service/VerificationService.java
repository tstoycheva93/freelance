package com.thesis.freelance.auth.service;

import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final String VERIFICATION_TYPE = "email_verification";
    private static final long VERIFICATION_EXPIRY_MINUTES = 10;

    @Transactional
    public void sendVerificationEmail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsVerified()) {
            throw new RuntimeException("Email already verified");
        }

        String token = createVerificationToken(user.getId().toString());
        String verificationLink = baseUrl + "/verify?token=" + token;

        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Transactional
    public void verifyEmail(String token) {
        Claims claims = validateAndGetClaims(token);

        String userIdStr = claims.getSubject();
        java.util.UUID userId = java.util.UUID.fromString(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsVerified()) {
            throw new RuntimeException("Email already verified");
        }

        user.setIsVerified(true);
        userRepository.save(user);

        // Send email after successful verification
        emailService.sendCongratulationsEmail(user.getEmail());

        log.info("Email verified for user: {}", user.getUsername());
    }

    private String createVerificationToken(String userId) {
        long expiryMillis = VERIFICATION_EXPIRY_MINUTES * 60 * 1000;
        return jwtTokenProvider.generateVerificationToken(userId, expiryMillis);
    }

    private Claims validateAndGetClaims(String token) {
        try {
            return jwtTokenProvider.validateAndGetClaims(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired verification token");
        }
    }
}
