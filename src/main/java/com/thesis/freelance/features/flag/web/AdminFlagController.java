package com.thesis.freelance.features.flag.web;

import com.thesis.freelance.features.flag.dto.AdminFlagDetailDto;
import com.thesis.freelance.features.flag.dto.AdminFlagListResponse;
import com.thesis.freelance.features.flag.dto.AdminFlagReviewRequest;
import com.thesis.freelance.features.flag.service.AdminFlagService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.UserSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/office/flags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFlagController {

    private final AdminFlagService adminFlagService;
    private final UserSecurityService userSecurityService;

    @GetMapping
    public ResponseEntity<AdminFlagListResponse> getFlags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String target
    ) {
        return ResponseEntity.ok(adminFlagService.getFlags(page, size, status, target));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminFlagDetailDto> getFlagDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(adminFlagService.getFlagDetail(id));
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<Map<String, String>> reviewFlag(
            @PathVariable UUID id,
            @Valid @RequestBody AdminFlagReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userSecurityService.findByUsername(userDetails.getUsername());
        adminFlagService.reviewFlag(id, request, admin.getId());
        return ResponseEntity.ok(Map.of("message", "Flag reviewed successfully"));
    }
}
