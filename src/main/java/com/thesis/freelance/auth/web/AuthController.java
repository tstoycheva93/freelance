package com.thesis.freelance.auth.web;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.auth.service.AuthMeService;
import com.thesis.freelance.auth.service.LoginService;
import com.thesis.freelance.auth.service.RegisterService;
import com.thesis.freelance.auth.service.VerificationService;
import com.thesis.freelance.features.user.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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


}
