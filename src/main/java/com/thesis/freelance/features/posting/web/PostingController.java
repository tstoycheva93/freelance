package com.thesis.freelance.features.posting.web;

import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.posting.dto.CreatePostingDto;
import com.thesis.freelance.features.posting.dto.PostingDetailDto;
import com.thesis.freelance.features.posting.dto.PostingTileDto;
import com.thesis.freelance.features.posting.dto.UpdatePostingDto;
import com.thesis.freelance.features.posting.service.PostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/postings")
@RequiredArgsConstructor
public class PostingController {
    
    private final PostingService postingService;
    
    @GetMapping
    public ResponseEntity<Page<PostingTileDto>> getAllPostings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) PostingStatus status,
            @RequestParam(required = false) UUID categoryId) {
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PostingTileDto> postings;
        if (status != null) {
            postings = postingService.getPostingsByStatus(status, pageable);
        } else if (categoryId != null) {
            postings = postingService.getPostingsByCategory(categoryId, pageable);
        } else {
            postings = postingService.getAllPostings(pageable);
        }
        
        return ResponseEntity.ok(postings);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostingDetailDto> getPostingById(@PathVariable UUID id) {
        return ResponseEntity.ok(postingService.getPostingById(id));
    }

    @PostMapping
    public ResponseEntity<PostingDetailDto> createPosting(
            @Valid @RequestBody CreatePostingDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (dto.attachmentUrls() != null) {
            for (int i = 0; i < dto.attachmentUrls().size(); i++) {
                System.out.println("  attachmentUrls[" + i + "]: " + dto.attachmentUrls().get(i));
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postingService.createPosting(dto, userDetails.getUsername()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PostingDetailDto> updatePosting(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostingDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postingService.updatePosting(id, dto, userDetails.getUsername()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosting(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postingService.deletePosting(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable UUID id) {
        postingService.incrementViews(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<PostingDetailDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam PostingStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postingService.updateStatus(id, status, userDetails.getUsername()));
    }
}
