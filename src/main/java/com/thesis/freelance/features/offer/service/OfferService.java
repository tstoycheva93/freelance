package com.thesis.freelance.features.offer.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.category.entity.Category;
import com.thesis.freelance.features.category.repository.CategoryRepository;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.offer.converter.OfferToOfferForPostingDtoConverter;
import com.thesis.freelance.features.offer.converter.OfferToOfferWithPostingDtoConverter;
import com.thesis.freelance.features.offer.converter.SkillListMapper;
import com.thesis.freelance.features.offer.dto.OfferForPostingDto;
import com.thesis.freelance.features.offer.dto.OfferWithPostingDto;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.skill.dto.SkillDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SkillListMapper skillListMapper;

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

        if (!offer.getPosting().getClient().getUserId().equals(requestingUserId)) {
            throw new SecurityException("Not authorized to update this offer");
        }

        offer.setStatus(newStatus);
        Offer savedOffer = offerRepository.save(offer);

        Freelancer freelancer = savedOffer.getFreelancer();
        UUID freelancerUserId = freelancer.getUserId();
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
}
