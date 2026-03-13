package com.thesis.freelance.features.profile.web;

import com.thesis.freelance.features.profile.dto.PeopleDto;
import com.thesis.freelance.features.profile.dto.PeopleResponseDto;
import com.thesis.freelance.features.profile.dto.ProfileResponseDto;
import com.thesis.freelance.features.posting.dto.PostingProfileDto;
import com.thesis.freelance.features.review.dto.ReviewProfileDto;
import com.thesis.freelance.features.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    public record PostingPageResponse(
            List<PostingProfileDto> content,
            boolean hasNext,
            long totalElements,
            int totalPages
    ) {}


    @GetMapping("/user/{username}")
    public ResponseEntity<ProfileResponseDto> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getProfileByUsername(username));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfileById(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<Page<ReviewProfileDto>> getUserReviews(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(profileService.getUserReviews(userId, pageable));
    }

    @GetMapping("/{userId}/postings")
    public ResponseEntity<PostingPageResponse> getUserPostings(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostingProfileDto> result = profileService.getUserPostings(userId, pageable);

        return ResponseEntity.ok(new PostingPageResponse(
                result.getContent(),
                result.hasNext(),
                result.getTotalElements(),
                result.getTotalPages()
        ));
    }


    @GetMapping("/{userId}/saved-postings")
    public ResponseEntity<PostingPageResponse> getSavedPostings(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostingProfileDto> result = profileService.getSavedPostings(userId, pageable);

        return ResponseEntity.ok(new PostingPageResponse(
                result.getContent(),
                result.hasNext(),
                result.getTotalElements(),
                result.getTotalPages()
        ));
    }


    @GetMapping("/people")
    public ResponseEntity<PeopleResponseDto> getPeople(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) UUID currentUserId) {
        
        Page<PeopleDto> peoplePage = profileService.getPeople(page, size, search, role, sortBy, sortOrder, currentUserId);
        
        PeopleResponseDto response = PeopleResponseDto.builder()
                .content(peoplePage.getContent())
                .hasNext(peoplePage.hasNext())
                .totalElements(peoplePage.getTotalElements())
                .totalPages(peoplePage.getTotalPages())
                .pageNumber(peoplePage.getNumber())
                .pageSize(peoplePage.getSize())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
