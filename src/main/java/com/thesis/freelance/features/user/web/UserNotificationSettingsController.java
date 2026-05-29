package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserNotificationSettingsDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserNotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/notification-settings")
@RequiredArgsConstructor
public class UserNotificationSettingsController {

    private final UserNotificationSettingsService userNotificationSettingsService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(userNotificationSettingsService.getSettings(user.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserNotificationSettingsDto dto) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(userNotificationSettingsService.updateSettings(user.getId(), dto));
    }
}
