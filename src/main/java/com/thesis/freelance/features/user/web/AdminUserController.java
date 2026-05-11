package com.thesis.freelance.features.user.web;

import com.thesis.freelance.features.user.dto.AdminUserDetailResponse;
import com.thesis.freelance.features.user.dto.AdminUserListResponse;
import com.thesis.freelance.features.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/office/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<AdminUserListResponse> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom
    ) {
        return ResponseEntity.ok(adminUserService.getUsers(page, size, role, status, dateFrom));
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<AdminUserDetailResponse> getUser(@PathVariable String identifier) {
        AdminUserDetailResponse user = adminUserService.getUser(identifier);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable UUID id) {
        adminUserService.suspendUser(id);
        return ResponseEntity.ok(Map.of("message", "User suspended"));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Map<String, String>> reactivateUser(@PathVariable UUID id) {
        adminUserService.reactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User reactivated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @PostMapping("/{id}/force-password-reset")
    public ResponseEntity<Map<String, String>> forcePasswordReset(@PathVariable UUID id) {
        adminUserService.forcePasswordReset(id);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/{id}/force-verify")
    public ResponseEntity<Map<String, String>> forceVerify(@PathVariable UUID id) {
        adminUserService.forceVerify(id);
        return ResponseEntity.ok(Map.of("message", "Verification email sent"));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> deletePhoto(@PathVariable UUID id) {
        adminUserService.deleteUserPhoto(id);
        return ResponseEntity.ok(Map.of("message", "Photo deleted"));
    }
}
