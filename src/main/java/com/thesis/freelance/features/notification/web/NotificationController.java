package com.thesis.freelance.features.notification.web;

import com.thesis.freelance.features.notification.dto.NotificationDto;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        Page<NotificationDto> notifications = notificationService.getNotificationsForUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        NotificationDto notification = notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok(notification);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    private UUID getCurrentUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
