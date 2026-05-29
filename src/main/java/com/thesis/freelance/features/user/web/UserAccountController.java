package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserAccountDto;
import com.thesis.freelance.features.user.dto.UserAccountUpdateDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(userAccountService.getAccount(user.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserAccountUpdateDto dto) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(userAccountService.updateAccount(user.getId(), dto));
    }
}
