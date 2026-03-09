package com.thesis.freelance.auth.web;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.auth.service.AuthMeService;
import com.thesis.freelance.auth.service.LoginService;
import com.thesis.freelance.auth.service.PasswordResetService;
import com.thesis.freelance.auth.service.RegisterService;
import com.thesis.freelance.auth.service.TwoFactorAuthService;
import com.thesis.freelance.auth.service.VerificationService;
import com.thesis.freelance.features.user.dto.UserDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final VerificationService verificationService;
    private final AuthMeService authMeService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterRequest registerRequest){
        registerService.registerUser(registerRequest);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse response = loginService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.sendResetEmail(email);
        return ResponseEntity.ok(Map.of(
                "message", "If an account exists, a reset link has been sent"
        ));
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<Map<String, String>> validateResetToken(@RequestParam String token) {
        passwordResetService.validateTokenOnly(token);
        return ResponseEntity.ok(Map.of("message", "Token is valid"));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, String>> sendVerification(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        verificationService.sendVerificationEmail(username);
        return ResponseEntity.ok(Map.of(
                "message", "Verification email sent successfully"
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        verificationService.verifyEmail(token);
        return ResponseEntity.ok(Map.of(
                "message", "Email verified successfully"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok(authMeService.getMe(authentication));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<LoginResponse> verify2FA(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String deviceId = request.get("deviceId");
        String deviceName = request.get("deviceName");

        if (code == null || code.isBlank()) {
            throw new RuntimeException("Verification code is required");
        }
        if (deviceId == null || deviceId.isBlank()) {
            throw new RuntimeException("Device ID is required");
        }

        TwoFactorAuthService.TokenData tokenData = twoFactorAuthService.verify2FACode(code, deviceId);

        User user = userRepository.findById(tokenData.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        twoFactorAuthService.trustDevice(tokenData.userId(), deviceId, deviceName);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        String jwtToken = jwtTokenProvider.generateToken(authentication);
        
        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwtToken)
                .requires2fa(false)
                .message("Login successful")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoleName())
                .fullName(user.getFullName())
                .photoUrl(user.getPhotoUrl())
                .userId(user.getId())
                .build());
    }


}
