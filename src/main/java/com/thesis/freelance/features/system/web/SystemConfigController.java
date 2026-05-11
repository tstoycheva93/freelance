package com.thesis.freelance.features.system.web;

import com.thesis.freelance.features.system.entity.SystemConfig;
import com.thesis.freelance.features.system.service.SystemConfigService;
import com.thesis.freelance.features.system.service.SystemStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final SystemStatsService systemStatsService;

    @GetMapping("/office/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemConfig> getConfig() {
        return ResponseEntity.ok(systemConfigService.getConfig());
    }

    @PutMapping("/office/system/registration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateRegistration(
            @RequestBody Map<String, Object> body
    ) {
        boolean enabled = (boolean) body.getOrDefault("enabled", true);
        String message = (String) body.get("message");

        systemConfigService.updateRegistration(enabled, message, currentUsername());

        return ResponseEntity.ok(Map.of(
                "message", enabled ? "Registration enabled" : "Registration disabled"
        ));
    }

    @PutMapping("/office/system/maintenance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleMaintenance(
            @RequestBody Map<String, Object> body
    ) {
        boolean enabled = (boolean) body.getOrDefault("enabled", false);
        String message = (String) body.get("message");

        systemConfigService.toggleMaintenance(enabled, message, currentUsername());

        return ResponseEntity.ok(Map.of(
                "message", enabled ? "Maintenance mode enabled" : "Maintenance mode disabled"
        ));
    }

    @GetMapping("/office/system/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(systemStatsService.getStats());
    }

    @GetMapping("/system/public/config")
    public ResponseEntity<Map<String, Object>> getPublicConfig() {
        SystemConfig config = systemConfigService.getConfig();

        return ResponseEntity.ok(Map.of(
                "maintenanceMode", config.isMaintenanceMode(),
                "maintenanceMessage", config.getMaintenanceMessage() != null
                        ? config.getMaintenanceMessage() : "",
                "registrationEnabled", config.isRegistrationEnabled(),
                "registrationMessage", config.getRegistrationMessage() != null
                        ? config.getRegistrationMessage() : ""
        ));
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
