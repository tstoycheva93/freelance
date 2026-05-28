package com.thesis.freelance.features.offer.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

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
import com.thesis.freelance.features.offer.entity.OfferContract;
import com.thesis.freelance.features.offer.entity.OfferReceipt;
import com.thesis.freelance.features.offer.exception.OfferNotFoundException;
import com.thesis.freelance.features.offer.exception.UnauthorizedOfferAccessException;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.offer.repository.OfferContractRepository;
import com.thesis.freelance.features.offer.repository.OfferReceiptRepository;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.offer.repository.ShortlistedOfferRepository;
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
import java.util.Map;
import java.util.UUID;
@Slf4j
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
    private final DocumentService documentService;
    private final OfferContractRepository offerContractRepository;
    private final OfferReceiptRepository offerReceiptRepository;
    private final ConversationRepository conversationRepository;
    private final ShortlistedOfferRepository shortlistedOfferRepository;
    private final MessageRepository messageRepository;

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
    public OfferForPostingDto updateOfferStatus(UUID offerId, OfferStatus newStatus, UUID requestingUserId, java.math.BigDecimal partialAmount) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        log.info("Attempting to change offer {} from status {} to status {} by user {}", offerId, offer.getStatus(), newStatus, requestingUserId);

        if (offer.getStatus() == newStatus) {
            log.info("Offer {} is already at status {}, returning early", offerId, newStatus);
            User freelancerUser = userRepository.findById(offer.getFreelancer().getUserId()).orElse(null);
            List<SkillDto> skills = getFreelancerSkills(offer.getFreelancer().getId());
            OfferForPostingDto dto = offerToOfferForPostingDtoConverter.convert(offer, offer.getFreelancer(), freelancerUser);
            dto.setSkills(skills);
            return dto;
        }

        boolean isClientOwner = offer.getPosting().getClient().getUserId().equals(requestingUserId);
        boolean isFreelancerOwner = offer.getFreelancer().getUserId() != null 
                && offer.getFreelancer().getUserId().equals(requestingUserId);
        
        if (!isClientOwner && !isFreelancerOwner) {
            throw new SecurityException("Not authorized to update this offer");
        }

        if (isFreelancerOwner && !isClientOwner) {
            if (newStatus != OfferStatus.CANCELED && newStatus != OfferStatus.PENDING && newStatus != OfferStatus.DELIVERED) {
                throw new IllegalStateException("Freelancers can only cancel, reactivate, or mark their offers as delivered");
            }
            if (newStatus == OfferStatus.PENDING && offer.getStatus() != OfferStatus.CANCELED) {
                throw new IllegalStateException("Can only reactivate canceled offers");
            }
            if (newStatus == OfferStatus.DELIVERED && offer.getStatus() != OfferStatus.ACCEPTED) {
                throw new IllegalStateException("Can only mark accepted offers as delivered");
            }
        }

        if (isClientOwner && !isFreelancerOwner) {
            if (newStatus == OfferStatus.COMPLETED && offer.getStatus() != OfferStatus.DELIVERED) {
                if (offer.getStatus() != OfferStatus.ACCEPTED || !java.time.LocalDate.now().isAfter(offer.getDeliveryTime())) {
                    throw new IllegalStateException("Can only complete delivered offers or accepted offers past delivery time");
                }
            }
            if (newStatus == OfferStatus.UNSATISFACTORY && offer.getStatus() != OfferStatus.DELIVERED) {
                if (offer.getStatus() != OfferStatus.ACCEPTED || !java.time.LocalDate.now().isAfter(offer.getDeliveryTime())) {
                    throw new IllegalStateException("Can only mark delivered offers or accepted offers past delivery time as unsatisfactory");
                }
            }
        }

        if (newStatus == OfferStatus.ACCEPTED) {
            offer.accept();
            OfferContract contract = offerContractRepository.findByOfferId(offer.getId())
                    .orElseThrow(() -> new IllegalStateException("Contract not found"));
            contract.setClientSigned(true);
            contract.setSignedAt(java.time.LocalDateTime.now());
            byte[] updatedPdf = documentService.generateContract(offer, true, true, contract.getSignedAt());
            contract.setContractPdf(updatedPdf);
            offerContractRepository.save(contract);
        } else if (newStatus == OfferStatus.REJECTED) {
            offer.reject();
        } else if (newStatus == OfferStatus.CANCELED) {
            offer.cancel();
        } else if (newStatus == OfferStatus.SHORTLISTED) {
            offer.shortlist();
        } else if (newStatus == OfferStatus.DELIVERED) {
            offer.markDelivered();
        } else if (newStatus == OfferStatus.COMPLETED) {
            offer.setStatus(OfferStatus.COMPLETED);
            log.info("Generating receipt for offer {} with amount {}", offer.getId(), offer.getPrice());
            byte[] receiptPdf = documentService.generateReceipt(offer, offer.getPrice());
            log.info("Receipt generated for offer {}, now saving receipt", offer.getId());
            OfferReceipt receipt = OfferReceipt.builder()
                    .offerId(offer.getId())
                    .generatedAt(java.time.LocalDateTime.now())
                    .receiptPdf(receiptPdf)
                    .settledAmount(offer.getPrice())
                    .build();
            offerReceiptRepository.save(receipt);
            log.info("Receipt saved for offer {}, now saving offer", offer.getId());
        } else if (newStatus == OfferStatus.UNSATISFACTORY) {
            offer.setStatus(OfferStatus.UNSATISFACTORY);
            java.math.BigDecimal amount = partialAmount != null ? partialAmount : java.math.BigDecimal.ZERO;
            log.info("Generating receipt for offer {} with amount {}", offer.getId(), amount);
            byte[] receiptPdf = documentService.generateReceipt(offer, amount);
            log.info("Receipt generated for offer {}, now saving receipt", offer.getId());
            OfferReceipt receipt = OfferReceipt.builder()
                    .offerId(offer.getId())
                    .generatedAt(java.time.LocalDateTime.now())
                    .receiptPdf(receiptPdf)
                    .settledAmount(amount)
                    .build();
            offerReceiptRepository.save(receipt);
            log.info("Receipt saved for offer {}, now saving offer", offer.getId());
        } else {
            offer.setStatus(newStatus);
        }

        Offer savedOffer = offerRepository.save(offer);
        log.info("Offer {} saved with status {}, now sending notifications", offerId, newStatus);

        Freelancer freelancer = savedOffer.getFreelancer();
        UUID freelancerUserId = freelancer.getUserId();
        UUID clientUserId = offer.getPosting().getClient().getUserId();

        if (newStatus == OfferStatus.ACCEPTED || newStatus == OfferStatus.REJECTED || newStatus == OfferStatus.CANCELED || newStatus == OfferStatus.DELIVERED || newStatus == OfferStatus.COMPLETED || newStatus == OfferStatus.UNSATISFACTORY) {
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
                } else if (newStatus == OfferStatus.CANCELED && !isFreelancerOwner) {
                    notificationService.notifyOfferCanceled(
                            freelancerUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                } else if (newStatus == OfferStatus.DELIVERED) {
                    notificationService.notifyOfferDelivered(
                            clientUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                } else if (newStatus == OfferStatus.COMPLETED) {
                    notificationService.notifyOfferCompleted(
                            freelancerUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                } else if (newStatus == OfferStatus.UNSATISFACTORY) {
                    notificationService.notifyOfferUnsatisfactory(
                            freelancerUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                    );
                }
            }
            if (newStatus == OfferStatus.CANCELED && isFreelancerOwner && clientUserId != null) {
                 notificationService.notifyOfferCanceled(
                            clientUserId,
                            savedOffer.getId(),
                            savedOffer.getPosting().getTitle()
                 );
            }
        }

        log.info("Notifications sent for offer {}", offerId);

        User freelancerUser = freelancerUserId != null ? userRepository.findById(freelancerUserId).orElse(null) : null;

        List<SkillDto> skills = getFreelancerSkills(freelancer.getId());

        OfferForPostingDto dto = offerToOfferForPostingDtoConverter.convert(savedOffer, freelancer, freelancerUser);
        dto.setSkills(skills);

        return dto;
    }
    
    @Transactional
    public void submitDeliverables(UUID offerId, List<String> fileUrls, UUID freelancerUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (!offer.getFreelancer().getUserId().equals(freelancerUserId)) {
            throw new SecurityException("Not authorized to submit deliverables for this offer");
        }

        if (offer.getStatus() != OfferStatus.ACCEPTED && offer.getStatus() != OfferStatus.DELIVERED) {
            throw new IllegalStateException("Can only submit deliverables for accepted or delivered offers");
        }

        offer.setDeliverableUrls(fileUrls);
        offerRepository.save(offer);
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
        
        // Generate contract
        byte[] contractPdf = documentService.generateContract(savedOffer, true, false, null);
        OfferContract contract = OfferContract.builder()
                .offerId(savedOffer.getId())
                .freelancerSigned(true)
                .clientSigned(false)
                .generatedAt(java.time.LocalDateTime.now())
                .contractPdf(contractPdf)
                .build();
        offerContractRepository.save(contract);
        
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
        Offer offer = offerRepository.findByIdWithAttachmentsAndDeliverables(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        Hibernate.initialize(offer.getAttachments());
        Hibernate.initialize(offer.getDeliverableUrls());

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
        Offer offer = offerRepository.findByIdWithAttachmentsAndDeliverables(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        UUID freelancerUserId = offer.getFreelancer().getUserId();
        UUID clientUserId = offer.getPosting().getClient().getUserId();
        
        boolean isFreelancerOwner = freelancerUserId != null && freelancerUserId.equals(requestingUserId);
        boolean isClientOwner = clientUserId != null && clientUserId.equals(requestingUserId);
        
        if (!isFreelancerOwner && !isClientOwner) {
            throw new UnauthorizedOfferAccessException();
        }

        messageRepository.deleteByOfferId(offerId);
        conversationRepository.deleteByOfferId(offerId);
        shortlistedOfferRepository.deleteByOfferId(offerId);
        offerContractRepository.deleteByOfferId(offerId);
        offerReceiptRepository.deleteByOfferId(offerId);

        offer.setAttachments(new ArrayList<>());
        offer.setDeliverableUrls(new ArrayList<>());
        offerRepository.delete(offer);
    }
    
    @Transactional(readOnly = true)
    public Offer getOfferEntityById(UUID offerId) {
         return offerRepository.findByIdWithDeliverables(offerId)
                 .orElseThrow(() -> new OfferNotFoundException(offerId));
    }

    @Transactional(readOnly = true)
    public byte[] getContractPdf(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
                
        checkOfferAccess(offer, requestingUserId);
        
        OfferContract contract = offerContractRepository.findByOfferId(offerId)
                .orElseThrow(() -> new IllegalStateException("Contract not found"));
                
        return contract.getContractPdf();
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getContractStatus(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
                
        checkOfferAccess(offer, requestingUserId);
        
        OfferContract contract = offerContractRepository.findByOfferId(offerId)
                .orElseThrow(() -> new IllegalStateException("Contract not found"));
                
        Map<String, Object> status = new java.util.HashMap<>();
        status.put("freelancerSigned", contract.isFreelancerSigned());
        status.put("clientSigned", contract.isClientSigned());
        status.put("generatedAt", contract.getGeneratedAt());
        status.put("signedAt", contract.getSignedAt());
        return status;
    }
    
    @Transactional(readOnly = true)
    public byte[] getReceiptPdf(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
                
        checkOfferAccess(offer, requestingUserId);
        
        OfferReceipt receipt = offerReceiptRepository.findByOfferId(offerId)
                .orElseThrow(() -> new IllegalStateException("Receipt not found"));
                
        return receipt.getReceiptPdf();
    }
    
    @Transactional(readOnly = true)
    public boolean doesReceiptExist(UUID offerId, UUID requestingUserId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
                
        checkOfferAccess(offer, requestingUserId);
        
        return offerReceiptRepository.existsByOfferId(offerId);
    }
    
    private void checkOfferAccess(Offer offer, UUID requestingUserId) {
        UUID freelancerUserId = offer.getFreelancer().getUserId();
        UUID clientUserId = offer.getPosting().getClient().getUserId();
        
        boolean isFreelancerOwner = freelancerUserId != null && freelancerUserId.equals(requestingUserId);
        boolean isClientOwner = clientUserId != null && clientUserId.equals(requestingUserId);
        
        if (!isFreelancerOwner && !isClientOwner) {
            throw new UnauthorizedOfferAccessException();
        }
    }
}
