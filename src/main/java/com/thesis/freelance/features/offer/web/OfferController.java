package com.thesis.freelance.features.offer.web;

import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.client.service.ClientPersonalService;
import com.thesis.freelance.features.freelancer.service.FreelancerPersonalService;
import com.thesis.freelance.features.offer.dto.CreateOfferRequestDto;
import com.thesis.freelance.features.offer.dto.OfferDetailDto;
import com.thesis.freelance.features.offer.dto.OfferForPostingDto;
import com.thesis.freelance.features.offer.dto.OfferWithPostingDto;
import com.thesis.freelance.features.offer.service.OfferService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final UserSecurityService userSecurityService;
    private final ClientPersonalService clientPersonalService;
    private final FreelancerPersonalService freelancerPersonalService;

    public record UnsatisfactoryRequest(BigDecimal partialAmount) {}

    @GetMapping
    public ResponseEntity<Page<OfferWithPostingDto>> getOffers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        UUID userId = user.getId();

        // Check if user is a client or freelancer
        var client = clientPersonalService.findByUserId(userId);
        var freelancer = freelancerPersonalService.findByUserId(userId);

        OfferStatus offerStatus = status != null ? OfferStatus.fromValue(status) : null;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

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
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        OfferStatus offerStatus = status != null ? OfferStatus.fromValue(status) : null;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OfferForPostingDto> offers = offerService.getOffersForPosting(
                postingId, user.getId(), offerStatus, pageable);

        return ResponseEntity.ok(offers);
    }

    @PatchMapping("/{offerId}/status")
    public ResponseEntity<OfferForPostingDto> updateOfferStatus(
            @PathVariable UUID offerId,
            @RequestParam OfferStatus status,
            @RequestBody(required = false) UnsatisfactoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        BigDecimal amount = request != null ? request.partialAmount() : null;
        OfferForPostingDto updatedOffer = offerService.updateOfferStatus(offerId, status, user.getId(), amount);
        return ResponseEntity.ok(updatedOffer);
    }

    @PostMapping("/{postingId}")
    public ResponseEntity<OfferForPostingDto> createOffer(
            @PathVariable UUID postingId,
            @RequestBody CreateOfferRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        // Validate user is freelancer
        if (!user.isFreelancer()) {
            throw new IllegalStateException("Only freelancers can create offers");
        }

        OfferForPostingDto createdOffer = offerService.createOffer(
                postingId, user.getId(), requestDto);

        return ResponseEntity.ok(createdOffer);
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<OfferDetailDto> getOfferById(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        OfferDetailDto offer = offerService.getOfferById(offerId, user.getId());
        return ResponseEntity.ok(offer);
    }

    @GetMapping("/check-active-offer")
    public ResponseEntity<java.util.Map<String, Object>> checkHasActiveOffer(
            @RequestParam UUID postingId,
            @RequestParam UUID freelancerUserId) {

        java.util.Map<String, Object> result = offerService.checkActiveOfferForPosting(postingId, freelancerUserId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        offerService.deleteOffer(offerId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deliverables")
    public ResponseEntity<Void> submitDeliverables(
            @PathVariable UUID id,
            @RequestBody List<String> fileUrls,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        offerService.submitDeliverables(id, fileUrls, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/deliverables")
    public ResponseEntity<List<String>> getDeliverables(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        com.thesis.freelance.features.offer.entity.Offer offer = offerService.getOfferEntityById(id);
        
        boolean isClientOwner = offer.getPosting().getClient().getUserId().equals(user.getId());
        boolean isFreelancerOwner = offer.getFreelancer().getUserId() != null 
                && offer.getFreelancer().getUserId().equals(user.getId());
        
        if (!isClientOwner && !isFreelancerOwner) {
            throw new SecurityException("Not authorized");
        }

        if (isClientOwner && !offer.isDeliverableVisible()) {
            throw new SecurityException("Deliverables are not yet visible");
        }

        return ResponseEntity.ok(offer.getDeliverableUrls());
    }

    @GetMapping("/{id}/contract")
    public ResponseEntity<byte[]> getContract(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        byte[] pdf = offerService.getContractPdf(id, user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contract-" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/{id}/contract/status")
    public ResponseEntity<Map<String, Object>> getContractStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(offerService.getContractStatus(id, user.getId()));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        byte[] pdf = offerService.getReceiptPdf(id, user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt-" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/{id}/receipt/exists")
    public ResponseEntity<Map<String, Boolean>> checkReceiptExists(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userSecurityService.findByUsername(userDetails.getUsername());

        boolean exists = offerService.doesReceiptExist(id, user.getId());
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
