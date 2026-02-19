package com.thesis.freelance.auth.web;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.auth.service.LoginService;
import com.thesis.freelance.auth.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final LoginService loginService;

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

}
