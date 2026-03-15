package com.thesis.freelance.features.offer.web;

import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.offer.dto.CreateOfferRequestDto;
import com.thesis.freelance.features.offer.dto.OfferForPostingDto;
import com.thesis.freelance.features.offer.dto.OfferWithPostingDto;
import com.thesis.freelance.features.offer.service.OfferService;
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
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;

    @GetMapping
    public ResponseEntity<Page<OfferWithPostingDto>> getOffers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UUID userId = user.getId();

        // Check if user is a client or freelancer
        var client = clientRepository.findByUserId(userId);
        var freelancer = freelancerRepository.findByUserId(userId);

        OfferStatus offerStatus = status != null ? OfferStatus.fromValue(status) : null;
        PageRequest pageable = PageRequest.of(page, size);

        Page<OfferWithPostingDto> offers;

        if (client.isPresent()) {
            // User is a client - get offers to their postings
            offers = offerService.getOffersForClient(userId, offerStatus, pageable);
        } else if (freelancer.isPresent()) {
            // User is a freelancer - get offers they've made
            offers = offerService.getOffersForFreelancer(userId, offerStatus, pageable);
        } else {
            throw new IllegalArgumentException("User is neither a client nor a freelancer");
        }

        return ResponseEntity.ok(offers);
    }

    @GetMapping("/posting/{postingId}")
    public ResponseEntity<Page<OfferForPostingDto>> getOffersForPosting(
            @PathVariable UUID postingId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OfferStatus offerStatus = status != null ? OfferStatus.fromValue(status) : null;
        PageRequest pageable = PageRequest.of(page, size);

        Page<OfferForPostingDto> offers = offerService.getOffersForPosting(
                postingId, user.getId(), offerStatus, pageable);

        return ResponseEntity.ok(offers);
    }

    @PatchMapping("/{offerId}/status")
    public ResponseEntity<OfferForPostingDto> updateOfferStatus(
            @PathVariable UUID offerId,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OfferStatus newStatus = OfferStatus.fromValue(status);

        OfferForPostingDto updatedOffer = offerService.updateOfferStatus(
                offerId, newStatus, user.getId());

        return ResponseEntity.ok(updatedOffer);
    }

    @PostMapping("/posting/{postingId}/offers")
    public ResponseEntity<OfferForPostingDto> createOffer(
            @PathVariable UUID postingId,
            @RequestBody CreateOfferRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate user is freelancer
        if (!user.isFreelancer()) {
            throw new IllegalStateException("Only freelancers can create offers");
        }

        OfferForPostingDto createdOffer = offerService.createOffer(
                postingId, user.getId(), requestDto);

        return ResponseEntity.ok(createdOffer);
    }

    @GetMapping("/check-active-offer")
    public ResponseEntity<java.util.Map<String, Object>> checkHasActiveOffer(
            @RequestParam UUID postingId,
            @RequestParam UUID freelancerUserId) {

        java.util.Map<String, Object> result = offerService.checkActiveOfferForPosting(postingId, freelancerUserId);
        return ResponseEntity.ok(result);
    }
}
