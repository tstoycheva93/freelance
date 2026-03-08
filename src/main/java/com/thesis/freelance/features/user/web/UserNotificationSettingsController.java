package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserNotificationSettingsDto;
import com.thesis.freelance.features.user.service.UserNotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/notification-settings")
@RequiredArgsConstructor
public class UserNotificationSettingsController {

    private final UserNotificationSettingsService userNotificationSettingsService;

    @GetMapping
    public ResponseEntity<?> getSettings(@RequestParam UUID userId) {
        return ResponseEntity.ok(userNotificationSettingsService.getSettings(userId));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(
            @RequestParam UUID userId,
            @RequestBody UserNotificationSettingsDto dto) {
        return ResponseEntity.ok(userNotificationSettingsService.updateSettings(userId, dto));
    }
}
