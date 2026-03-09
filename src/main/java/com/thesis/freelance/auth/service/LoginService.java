package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.common.exception.InvalidCredentialsException;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TwoFactorAuthService twoFactorAuthService;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException());

            String deviceId = loginRequest.getDeviceId();
            String deviceName = loginRequest.getDeviceName();

            if (user.getIs2faEnabled()) {
                // Reject outright if the client didn't supply a device ID
                if (deviceId == null || deviceId.isBlank()) {
                    return LoginResponse.builder()
                            .requires2fa(true)
                            .message("Two-factor authentication is required")
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .role(user.getRoleName())
                            .fullName(user.getFullName())
                            .photoUrl(user.getPhotoUrl())
                            .userId(user.getId())
                            .build();
                }

                if (!twoFactorAuthService.isDeviceTrusted(user.getId(), deviceId)) {
                    twoFactorAuthService.send2FAEmail(user.getId(), user.getEmail(), deviceId, deviceName);

                    return LoginResponse.builder()
                            .requires2fa(true)
                            .message("Check your email to complete login")
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .role(user.getRoleName())
                            .fullName(user.getFullName())
                            .photoUrl(user.getPhotoUrl())
                            .userId(user.getId())
                            .build();
                }
                // Device is already trusted — proceed to issue JWT, no re-trust needed
            }

            String token = jwtTokenProvider.generateToken(authentication);

            return LoginResponse.builder()
                    .token(token)
                    .requires2fa(false)
                    .message("Login successful")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRoleName())
                    .fullName(user.getFullName())
                    .photoUrl(user.getPhotoUrl())
                    .userId(user.getId())
                    .build();

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

}
