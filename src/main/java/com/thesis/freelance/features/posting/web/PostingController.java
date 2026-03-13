package com.thesis.freelance.features.posting.web;

import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.posting.dto.CreatePostingDto;
import com.thesis.freelance.features.posting.dto.PostingDetailDto;
import com.thesis.freelance.features.posting.dto.PostingTileDto;
import com.thesis.freelance.features.posting.dto.UpdatePostingDto;
import com.thesis.freelance.features.posting.service.PostingService;
import com.thesis.freelance.features.posting.service.SavedPostingService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
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
    private final SavedPostingService savedPostingService;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<Page<PostingTileDto>> getAllPostings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PostingStatus postingStatus = status != null ? PostingStatus.fromValue(status) : null;
        Page<PostingTileDto> postings = postingService.getPostings(postingStatus, search, categoryId, locationId, pageable);
        
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
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postingService.updateStatus(id, PostingStatus.fromValue(status), userDetails.getUsername()));
    }

    @GetMapping("/{id}/saved")
    public ResponseEntity<Boolean> isPostingSaved(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean isSaved = savedPostingService.isSavedByFreelancer(user.getId(), id);
        return ResponseEntity.ok(isSaved);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> savePosting(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        savedPostingService.savePosting(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsavePosting(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        savedPostingService.unsavePosting(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

}
