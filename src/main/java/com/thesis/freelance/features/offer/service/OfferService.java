package com.thesis.freelance.features.offer.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.Availability;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.freelancer.dto.FreelancerCVDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.offer.converter.OfferToOfferDetailDtoConverter;
import com.thesis.freelance.features.offer.converter.OfferToOfferForPostingDtoConverter;
import com.thesis.freelance.features.offer.converter.OfferToOfferWithPostingDtoConverter;
import com.thesis.freelance.features.offer.converter.SkillListMapper;
import com.thesis.freelance.features.offer.dto.CreateOfferRequestDto;
import com.thesis.freelance.features.offer.dto.OfferDetailDto;
import com.thesis.freelance.features.offer.dto.OfferForPostingDto;
import com.thesis.freelance.features.offer.dto.OfferWithPostingDto;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.offer.exception.OfferNotFoundException;
import com.thesis.freelance.features.offer.exception.UnauthorizedOfferAccessException;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.skill.dto.SkillDto;
import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import com.thesis.freelance.features.uploadedCV.repository.UploadedCVRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostingRepository postingRepository;

    private final OfferToOfferWithPostingDtoConverter offerToOfferWithPostingDtoConverter;
    private final OfferToOfferForPostingDtoConverter offerToOfferForPostingDtoConverter;
    private final OfferToOfferDetailDtoConverter offerToOfferDetailDtoConverter;
    private final SkillListMapper skillListMapper;
    private final FreelancerRepository freelancerRepository;
    private final UploadedCVRepository uploadedCVRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<OfferWithPostingDto> getOffersForFreelancer(UUID freelancerUserId, OfferStatus status, Pageable pageable) {
        Page<Offer> offersPage;
        
        if (status != null) {
            offersPage = offerRepository.findByFreelancerUserIdAndActivityStatusAndStatus(
                    freelancerUserId, ActivityStatus.ACTIVE, status, pageable);
        } else {
            offersPage = offerRepository.findByFreelancerUserIdAndActivityStatus(
                    freelancerUserId, ActivityStatus.ACTIVE, pageable);
        }

        return offersPage.map(offer -> {
            Posting posting = offer.getPosting();
            Client client = posting.getClient();
            UUID clientUserId = client.getUserId();
            User clientUser = clientUserId != null ? userRepository.findById(clientUserId).orElse(null) : null;
            
            String categoryName = getCategoryName(posting.getCategoryId());
            
            OfferWithPostingDto dto = offerToOfferWithPostingDtoConverter.convertForFreelancer(offer, clientUser);
            setCategoryName(dto, categoryName);
            
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public Page<OfferWithPostingDto> getOffersForClient(UUID clientUserId, OfferStatus status, Pageable pageable) {
        Page<Offer> offersPage;
        
        if (status != null) {
            offersPage = offerRepository.findByPostingClientUserIdAndActivityStatusAndStatus(
                    clientUserId, ActivityStatus.ACTIVE, status, pageable);
        } else {
            offersPage = offerRepository.findByPostingClientUserIdAndActivityStatus(
                    clientUserId, ActivityStatus.ACTIVE, pageable);
        }

        return offersPage.map(offer -> {
            Freelancer freelancer = offer.getFreelancer();
            UUID freelancerUserId = freelancer.getUserId();
            User freelancerUser = freelancerUserId != null ? userRepository.findById(freelancerUserId).orElse(null) : null;
            
            String categoryName = getCategoryName(offer.getPosting().getCategoryId());
            
            List<SkillDto> skills = getFreelancerSkills(freelancer.getId());
            
            OfferWithPostingDto dto = offerToOfferWithPostingDtoConverter.convertForClient(offer, freelancer, freelancerUser);
            setCategoryName(dto, categoryName);
            dto.setSkills(skills);
            
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public Page<OfferForPostingDto> getOffersForPosting(UUID postingId, UUID currentUserId, OfferStatus status, Pageable pageable) {
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("Posting not found"));

        if (!posting.getClient().getUserId().equals(currentUserId)) {
            throw new SecurityException("Not authorized to view offers for this posting");
        }

        Page<Offer> offersPage;
        
        if (status != null) {
            offersPage = offerRepository.findByPostingIdAndActivityStatusAndStatus(
                    postingId, ActivityStatus.ACTIVE, status, pageable);
        } else {
            offersPage = offerRepository.findByPostingIdAndActivityStatus(
                    postingId, ActivityStatus.ACTIVE, pageable);
        }

        return offersPage.map(offer -> {
            Freelancer freelancer = offer.getFreelancer();
            UUID freelancerUserId = freelancer.getUserId();
            User freelancerUser = freelancerUserId != null ? userRepository.findById(freelancerUserId).orElse(null) : null;
            
            List<SkillDto> skills = getFreelancerSkills(freelancer.getId());
            
            OfferForPostingDto dto = offerToOfferForPostingDtoConverter.convert(offer, freelancer, freelancerUser);
            dto.setSkills(skills);
            
            return dto;
        });
    }

    @Transactional
    public OfferForPostingDto updateOfferStatus(UUID offerId, OfferStatus newStatus, UUID requestingUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        boolean isClientOwner = offer.getPosting().getClient().getUserId().equals(requestingUserId);
        boolean isFreelancerOwner = offer.getFreelancer().getUserId() != null 
                && offer.getFreelancer().getUserId().equals(requestingUserId);
        
        if (!isClientOwner && !isFreelancerOwner) {
            throw new SecurityException("Not authorized to update this offer");
        }

        if (isFreelancerOwner && !isClientOwner) {
            if (newStatus != OfferStatus.CANCELED && newStatus != OfferStatus.PENDING) {
                throw new IllegalStateException("Freelancers can only cancel or reactivate their offers");
            }
            if (newStatus == OfferStatus.PENDING && offer.getStatus() != OfferStatus.CANCELED) {
                throw new IllegalStateException("Can only reactivate canceled offers");
            }
        }

        offer.setStatus(newStatus);
        Offer savedOffer = offerRepository.save(offer);

        Freelancer freelancer = savedOffer.getFreelancer();
        UUID freelancerUserId = freelancer.getUserId();
        UUID clientUserId = offer.getPosting().getClient().getUserId();

        if (newStatus == OfferStatus.ACCEPTED || newStatus == OfferStatus.REJECTED || newStatus == OfferStatus.CANCELED) {
            if (freelancerUserId != null) {
                if (newStatus == OfferStatus.ACCEPTED) {
                    notificationService.notifyOfferAccepted(
                            freelancerUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                } else if (newStatus == OfferStatus.REJECTED){
                    notificationService.notifyOfferRejected(
                            freelancerUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                }
                else {
                    notificationService.notifyOfferCanceled(
                            clientUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                }
            }
        }

        User freelancerUser = freelancerUserId != null ? userRepository.findById(freelancerUserId).orElse(null) : null;

        List<SkillDto> skills = getFreelancerSkills(freelancer.getId());

        OfferForPostingDto dto = offerToOfferForPostingDtoConverter.convert(savedOffer, freelancer, freelancerUser);
        dto.setSkills(skills);

        return dto;
    }

    private String getCategoryName(UUID categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .map(Category::getName)
                .orElse(null);
    }

    private List<SkillDto> getFreelancerSkills(UUID freelancerId) {
        List<FreelancerSkills> skills = freelancerSkillsRepository.findByFreelancerIdWithSkill(freelancerId, ActivityStatus.ACTIVE);
        return skillListMapper.apply(skills);
    }

    private void setCategoryName(OfferWithPostingDto dto, String categoryName) {
        try {
            var field = OfferWithPostingDto.class.getDeclaredField("categoryName");
            field.setAccessible(true);
            field.set(dto, categoryName);
        } catch (Exception e) {
        }
    }

    @Transactional
    public OfferForPostingDto createOffer(UUID postingId, UUID freelancerUserId, CreateOfferRequestDto requestDto) {
        // 1. Get posting and validate
        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("Posting not found"));

        if (posting.getStatus() == PostingStatus.CLOSED) {
            throw new IllegalStateException("Cannot apply to closed posting");
        }

        // 2. Get freelancer and validate
        Freelancer freelancer = freelancerRepository.findByUserId(freelancerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        if (freelancer.getAvailability() == Availability.NOT_AVAILABLE) {
            throw new IllegalStateException("Freelancer is not available");
        }

        if (freelancer.getCompleteness() < 100) {
            throw new IllegalStateException("Freelancer profile must be complete to apply");
        }

        // 3. Check for existing active offer
        if (offerRepository.hasActiveOfferByPostingAndFreelancer(
                postingId, freelancerUserId, ActivityStatus.ACTIVE)) {
            throw new IllegalStateException("You already have an active offer for this posting");
        }

        // 4. Get CV from freelancer
        UploadedCV cv = uploadedCVRepository.findFirstByFreelancer_IdOrderByUploadedAtDesc(freelancer.getId(), ActivityStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Freelancer must have a CV uploaded"));

        // 5. Create offer entity
        Offer offer = Offer.builder()
                .posting(posting)
                .freelancer(freelancer)
                .price(requestDto.getPrice())
                .message(requestDto.getMessage())
                .deliveryTime(requestDto.getDeliveryTime())
                .status(OfferStatus.PENDING)
                .build();

        // 6. Add CV attachment
        if (requestDto.getAttachmentUrls() != null && !requestDto.getAttachmentUrls().isEmpty()) {
            offer.setAttachments(requestDto.getAttachmentUrls());
        }

        // 7. Save and return
        Offer savedOffer = offerRepository.save(offer);
        freelancer.incrementOffers();

        // 8. Notify client about new offer
        UUID clientUserId = posting.getClient().getUserId();
        if (clientUserId != null) {
            notificationService.notifyNewOffer(
                    clientUserId,
                    savedOffer.getId(),
                    posting.getTitle()
            );
        }

        // Prepare response DTO
        User freelancerUser = userRepository.findById(freelancer.getUserId()).orElse(null);
        List<SkillDto> skills = getFreelancerSkills(freelancer.getId());

        OfferForPostingDto dto = offerToOfferForPostingDtoConverter.convert(savedOffer, freelancer, freelancerUser);
        dto.setSkills(skills);

        return dto;
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> checkActiveOfferForPosting(UUID postingId, UUID freelancerUserId) {
        boolean hasActiveOffer = offerRepository.hasActiveOfferByPostingAndFreelancer(
                postingId, freelancerUserId, ActivityStatus.ACTIVE);
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("hasActiveOffer", hasActiveOffer);
        
        if (hasActiveOffer) {
            java.util.UUID offerId = offerRepository.findActiveOfferIdByPostingAndFreelancer(
                    postingId, freelancerUserId, ActivityStatus.ACTIVE).orElse(null);
            if (offerId != null) {
                result.put("offerId", offerId.toString());
            }
        }
        
        return result;
    }

    @Transactional(readOnly = true)
    public OfferDetailDto getOfferById(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findByIdWithAttachments(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        Posting posting = offer.getPosting();
        Freelancer freelancer = offer.getFreelancer();
        UUID freelancerUserId = freelancer.getUserId();
        User freelancerUser = userRepository.findById(freelancerUserId).orElse(null);
        
        boolean isClientOwner = posting.getClient().getUserId().equals(requestingUserId);
        boolean isFreelancerOwner = freelancerUserId != null && freelancerUserId.equals(requestingUserId);
        
        if (!isClientOwner && !isFreelancerOwner) {
            throw new SecurityException("Not authorized to view this offer");
        }

        UploadedCV cv = uploadedCVRepository.findFirstByFreelancer_IdOrderByUploadedAtDesc(
                freelancer.getId(), ActivityStatus.ACTIVE).orElse(null);

        return offerToOfferDetailDtoConverter.convert(offer, posting, freelancer, freelancerUser, cv);
    }

    @Transactional
    public void deleteOffer(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findByIdWithAttachments(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        UUID freelancerUserId = offer.getFreelancer().getUserId();
        UUID clientUserId = offer.getPosting().getClient().getUserId();
        
        boolean isFreelancerOwner = freelancerUserId != null && freelancerUserId.equals(requestingUserId);
        boolean isClientOwner = clientUserId != null && clientUserId.equals(requestingUserId);
        
        if (!isFreelancerOwner && !isClientOwner) {
            throw new UnauthorizedOfferAccessException();
        }

        offer.setAttachments(new ArrayList<>());
        offerRepository.delete(offer);
    }
}
