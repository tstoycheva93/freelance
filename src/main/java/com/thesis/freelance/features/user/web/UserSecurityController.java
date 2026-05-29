package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.ChangePasswordDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserSecurityController {

    private final UserSecurityService userSecurityService;
    private final UserRepository userRepository;

    @GetMapping("/2fa")
    public ResponseEntity<?> get2FAStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean isEnabled = userSecurityService.get2FAStatus(user.getId());
        return ResponseEntity.ok().body(java.util.Map.of("isEnabled", isEnabled));
    }

    @PutMapping("/2fa")
    public ResponseEntity<?> toggle2FA(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody java.util.Map<String, Boolean> request) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Boolean enabled = request.get("enabled");
        userSecurityService.set2FAStatus(user.getId(), enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/2fa/devices")
    public ResponseEntity<?> getTrustedDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(userSecurityService.getTrustedDevices(user.getId()));
    }

    @PutMapping("/security/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordDto dto) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userSecurityService.changePassword(user.getId(), dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/security/account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userSecurityService.deleteAccount(user.getId());
        return ResponseEntity.ok().build();
    }
}
