package com.thesis.freelance.auth.service;

import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url-front}")
    private String baseUrl;

    private static final long TOKEN_EXPIRY_MINUTES = 5;

    public void sendResetEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = generateResetToken(user.getId().toString());
            String resetLink = baseUrl + "/reset-password?token=" + token;

            emailService.sendPasswordResetEmail(email, resetLink);
            log.info("Password reset email sent to: {}", email);
        });
    }

    public String generateResetToken(String userId) {
        long expiryMillis = TOKEN_EXPIRY_MINUTES * 60 * 1000;
        return jwtTokenProvider.generateVerificationToken(userId, expiryMillis);
    }

    public UUID validateResetToken(String token) {
        try {
            Claims claims = jwtTokenProvider.validateAndGetClaims(token);
            String userId = claims.getSubject();
            return UUID.fromString(userId);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired reset token");
        }
    }

    public void validateTokenOnly(String token) {
        validateResetToken(token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        UUID userId = validateResetToken(token);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", userId);
    }
}
