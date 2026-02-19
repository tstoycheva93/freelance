package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.exception.AccountDisabledException;
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

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Get user details from database
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException());

            // Check if account is active
            if (user.getActivityStatus() != ActivityStatus.ACTIVE) {
                throw new AccountDisabledException("Account is deactivated");
            }

            String token = jwtTokenProvider.generateToken(authentication);

            return LoginResponse.builder()
                    .token(token)
                    .message("Login successful")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRoleName())
                    .fullName(user.getFullName())
                    .photoUrl(user.getPhotoUrl())
                    .build();

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

}
