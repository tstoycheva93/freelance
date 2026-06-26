package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.entity.TrustedDevice;
import com.thesis.freelance.auth.repository.TrustedDeviceRepository;
import com.thesis.freelance.common.exception.ExpiredVerificationCodeException;
import com.thesis.freelance.common.exception.InvalidVerificationCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthService {

    private final TrustedDeviceRepository trustedDeviceRepository;
    private final EmailService emailService;
    private static final long TOKEN_EXPIRY_MINUTES = 5;

    private final ConcurrentHashMap<String, CodeEntry> verificationCodes = new ConcurrentHashMap<>();

    private static class CodeEntry {
        UUID userId;
        String deviceId;
        Instant expiry;

        CodeEntry(UUID userId, String deviceId, Instant expiry) {
            this.userId = userId;
            this.deviceId = deviceId;
            this.expiry = expiry;
        }
    }

    @Transactional
    public void send2FAEmail(UUID userId, String email, String deviceId, String deviceName) {
        String code = generate2FACode(userId, deviceId);

        emailService.sendTwoFactorAuthEmail(email, code, deviceName);
        log.info("2FA email sent to: {} for device: {}", email, deviceName);
    }

    public String generate2FACode(UUID userId, String deviceId) {
        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        Instant expiry = Instant.now().plusSeconds(TOKEN_EXPIRY_MINUTES * 60);
        
        verificationCodes.put(code, new CodeEntry(userId, deviceId, expiry));
        
        log.debug("Generated 2FA code for user: {}, device: {}", userId, deviceId);
        return code;
    }

    public TokenData verify2FACode(String code, String deviceId) {
        CodeEntry entry = verificationCodes.get(code);

        if (entry == null) {
            throw new InvalidVerificationCodeException();
        }

        if (entry.expiry.isBefore(Instant.now())) {
            verificationCodes.remove(code);
            throw new ExpiredVerificationCodeException();
        }

        if (!entry.deviceId.equals(deviceId)) {
            throw new InvalidVerificationCodeException();
        }

        verificationCodes.remove(code);

        log.debug("Verified 2FA code for user: {}, device: {}", entry.userId, entry.deviceId);
        return new TokenData(entry.userId, entry.deviceId);
    }

    public boolean isDeviceTrusted(UUID userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return false;
        }
        return trustedDeviceRepository.existsByUserIdAndDeviceId(userId, deviceId);
    }

    @Transactional
    public void trustDevice(UUID userId, String deviceId, String deviceName) {
        if (deviceId == null || deviceId.isBlank()) {
            log.warn("Cannot trust device with empty deviceId for user: {}", userId);
            return;
        }

        if (!trustedDeviceRepository.existsByUserIdAndDeviceId(userId, deviceId)) {
            TrustedDevice trustedDevice = TrustedDevice.builder()
                    .userId(userId)
                    .deviceId(deviceId)
                    .deviceName(deviceName != null ? deviceName : "Unknown Device")
                    .build();
            
            trustedDeviceRepository.save(trustedDevice);
            log.info("Device trusted for user: {}, device: {}", userId, deviceName);
        }
    }

    public record TokenData(UUID userId, String deviceId) {}
}
