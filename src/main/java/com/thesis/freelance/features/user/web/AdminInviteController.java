package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.*;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.AdminInviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/office/admins")
@RequiredArgsConstructor
public class AdminInviteController {

    private final AdminInviteService adminInviteService;
    private final UserRepository userRepository;

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> inviteAdmin(
            @Valid @RequestBody AdminInviteRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        adminInviteService.sendInvite(request, admin.getId());
        return ResponseEntity.ok(Map.of("message", "Invitation sent successfully"));
    }

    @GetMapping("/invites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminInviteDto>> getInvites() {
        return ResponseEntity.ok(adminInviteService.getInvites());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminDto>> getActiveAdmins() {
        return ResponseEntity.ok(adminInviteService.getActiveAdmins());
    }
}
