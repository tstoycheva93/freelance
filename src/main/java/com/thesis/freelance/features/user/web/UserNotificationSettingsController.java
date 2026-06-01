package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.UserNotificationSettingsDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserNotificationSettingsService;
import com.thesis.freelance.features.user.service.UserSecurityService;
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
    private final UserSecurityService userSecurityService;

    @GetMapping
    public ResponseEntity<?> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userSecurityService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userNotificationSettingsService.getSettings(user.getId()));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserNotificationSettingsDto dto) {
        User user = userSecurityService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userNotificationSettingsService.updateSettings(user.getId(), dto));
    }
}
