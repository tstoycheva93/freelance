package com.thesis.freelance.auth.web;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.auth.service.LoginService;
import com.thesis.freelance.auth.service.RegisterService;
import com.thesis.freelance.auth.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final VerificationService verificationService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest){
        registerService.registerUser(registerRequest);
        return ResponseEntity.ok("Successful registration.");
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

}
