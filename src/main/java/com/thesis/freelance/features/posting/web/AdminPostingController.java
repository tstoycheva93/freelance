package com.thesis.freelance.features.posting.web;

import com.thesis.freelance.features.posting.dto.AdminPostingDetailDto;
import com.thesis.freelance.features.posting.dto.AdminPostingListResponse;
import com.thesis.freelance.features.posting.service.AdminPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/office/postings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostingController {

    private final AdminPostingService adminPostingService;

    @GetMapping
    public ResponseEntity<AdminPostingListResponse> getPostings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String flagged,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(adminPostingService.getPostings(page, size, status, flagged, dateFrom, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminPostingDetailDto> getPostingDetail(@PathVariable UUID id) {
        AdminPostingDetailDto detail = adminPostingService.getPostingDetail(id);
        if (detail == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivatePosting(@PathVariable UUID id) {
        adminPostingService.deactivatePosting(id);
        return ResponseEntity.ok(Map.of("message", "Posting deactivated"));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, String>> closePosting(@PathVariable UUID id) {
        adminPostingService.closePosting(id);
        return ResponseEntity.ok(Map.of("message", "Posting closed"));
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<Map<String, String>> reopenPosting(@PathVariable UUID id) {
        adminPostingService.reopenPosting(id);
        return ResponseEntity.ok(Map.of("message", "Posting reopened"));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Map<String, String>> reactivatePosting(@PathVariable UUID id) {
        adminPostingService.reactivatePosting(id);
        return ResponseEntity.ok(Map.of("message", "Posting reactivated"));
    }
}
