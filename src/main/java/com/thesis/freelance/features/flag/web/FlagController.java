package com.thesis.freelance.features.flag.web;

import com.thesis.freelance.common.enums.FlagStatus;
import com.thesis.freelance.common.enums.FlagTarget;
import com.thesis.freelance.features.flag.entity.Flag;
import com.thesis.freelance.features.flag.service.FlagService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/flags")
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Flag> createFlag(
            @Valid @RequestBody CreateFlagRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User reporter = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Flag flag = flagService.createFlag(request.target(), request.targetId(), reporter.getId());
        
        if (flag == null) {
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.ok(flag);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Flag>> getPendingFlags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Flag> flags = flagService.getPendingFlags(PageRequest.of(page, size));
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flag> getFlagById(@PathVariable UUID id) {
        Flag flag = flagService.getFlagById(id);
        return ResponseEntity.ok(flag);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flag> reviewFlag(
            @PathVariable UUID id,
            @Valid @RequestBody FlagReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        
        Flag flag = flagService.reviewFlag(id, request.status(), request.reason(), admin.getId());
        return ResponseEntity.ok(flag);
    }

    public record CreateFlagRequest(FlagTarget target, UUID targetId) {}

    public record FlagReviewRequest(FlagStatus status, String reason) {}
}