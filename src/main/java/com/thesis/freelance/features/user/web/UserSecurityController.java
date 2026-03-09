package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.ChangePasswordDto;
import com.thesis.freelance.features.user.service.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserSecurityController {
//TODO: use the authenticated user in the controller, dont pass param from frontend and pass username to service methods
    private final UserSecurityService userSecurityService;

    @GetMapping("/2fa")
    public ResponseEntity<?> get2FAStatus(@RequestParam UUID userId) {
        boolean isEnabled = userSecurityService.get2FAStatus(userId);
        return ResponseEntity.ok().body(java.util.Map.of("isEnabled", isEnabled));
    }

    @PutMapping("/2fa")
    public ResponseEntity<?> toggle2FA(
            @RequestParam UUID userId,
            @RequestBody java.util.Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        userSecurityService.set2FAStatus(userId, enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/2fa/devices")
    public ResponseEntity<?> getTrustedDevices(@RequestParam UUID userId) {
        return ResponseEntity.ok(userSecurityService.getTrustedDevices(userId));
    }

    @PutMapping("/security/password")
    public ResponseEntity<?> changePassword(
            @RequestParam UUID userId,
            @Valid @RequestBody ChangePasswordDto dto) {
        userSecurityService.changePassword(userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/security/account")
    public ResponseEntity<?> deleteAccount(@RequestParam UUID userId) {
        userSecurityService.deleteAccount(userId);
        return ResponseEntity.ok().build();
    }
}
