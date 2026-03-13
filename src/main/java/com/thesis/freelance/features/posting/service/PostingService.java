package com.thesis.freelance.features.posting.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.location.repository.LocationRepository;
import com.thesis.freelance.features.posting.converter.PostingToDetailDtoConverter;
import com.thesis.freelance.features.posting.converter.PostingToTileDtoConverter;
import com.thesis.freelance.features.posting.dto.CreatePostingDto;
import com.thesis.freelance.features.posting.dto.PostingDetailDto;
import com.thesis.freelance.features.posting.dto.PostingTileDto;
import com.thesis.freelance.features.posting.dto.UpdatePostingDto;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.entity.PostingSkills;
import com.thesis.freelance.features.posting.exception.PostingNotFoundException;
import com.thesis.freelance.features.posting.exception.UnauthorizedPostingAccessException;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.posting.repository.PostingSkillsRepository;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.skill.repository.SkillRepository;
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
public class PostingService {
    
    private final PostingRepository postingRepository;
    private final PostingSkillsRepository postingSkillsRepository;
    private final ClientRepository clientRepository;
    private final LocationRepository locationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final PostingToTileDtoConverter postingToTileDtoConverter;
    private final PostingToDetailDtoConverter postingToDetailDtoConverter;
    
    @Transactional(readOnly = true)
    public Page<PostingTileDto> getAllPostings(Pageable pageable) {
        Page<Posting> postings = postingRepository.findAllWithClientAndLocation(ActivityStatus.ACTIVE, pageable);
        return postings.map(postingToTileDtoConverter::convert);
    }
    
    @Transactional(readOnly = true)
    public Page<PostingTileDto> getPostingsByStatus(PostingStatus status, Pageable pageable) {
        Page<Posting> postings = postingRepository.findAllByStatus(ActivityStatus.ACTIVE, status, pageable);
        return postings.map(postingToTileDtoConverter::convert);
    }
    
    @Transactional(readOnly = true)
    public Page<PostingTileDto> getPostingsByCategory(UUID categoryId, Pageable pageable) {
        Page<Posting> postings = postingRepository.findAllByCategoryId(ActivityStatus.ACTIVE, categoryId, pageable);
        return postings.map(postingToTileDtoConverter::convert);
    }

    @Transactional(readOnly = true)
    public Page<PostingTileDto> getPostings(PostingStatus status, String search, UUID categoryId, UUID locationId, Pageable pageable) {
        boolean hasStatus = status != null;
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasCategory = categoryId != null;
        boolean hasLocation = locationId != null;

        Page<Posting> postings;

        if (hasStatus) {
            postings = postingRepository.findAllByStatusAndSearchAndCategoryAndLocation(
                    ActivityStatus.ACTIVE, status, search, categoryId, locationId, pageable);
        } else if (hasSearch && hasCategory && hasLocation) {
            postings = postingRepository.findAllBySearchAndCategoryAndLocation(
                    ActivityStatus.ACTIVE, search, categoryId, locationId, pageable);
        } else if (hasSearch && hasCategory) {
            postings = postingRepository.findAllBySearchAndCategoryAndLocation(
                    ActivityStatus.ACTIVE, search, categoryId, null, pageable);
        } else if (hasSearch && hasLocation) {
            postings = postingRepository.findAllBySearchAndLocation(
                    ActivityStatus.ACTIVE, search, locationId, pageable);
        } else if (hasCategory && hasLocation) {
            postings = postingRepository.findAllByCategoryAndLocation(
                    ActivityStatus.ACTIVE, categoryId, locationId, pageable);
        } else if (hasSearch) {
            postings = postingRepository.findAllBySearch(ActivityStatus.ACTIVE, search, pageable);
        } else if (hasCategory) {
            postings = postingRepository.findAllByCategoryId(ActivityStatus.ACTIVE, categoryId, pageable);
        } else if (hasLocation) {
            postings = postingRepository.findAllByLocationId(ActivityStatus.ACTIVE, locationId, pageable);
        } else {
            postings = postingRepository.findAllWithClientAndLocation(ActivityStatus.ACTIVE, pageable);
        }

        return postings.map(postingToTileDtoConverter::convert);
    }
    
    @Transactional(readOnly = true)
    public PostingDetailDto getPostingById(UUID id) {
        Posting posting = postingRepository.findByIdWithClientAndLocation(ActivityStatus.ACTIVE, id);
        if (posting == null) {
            throw new PostingNotFoundException(id);
        }
        return postingToDetailDtoConverter.convert(posting);
    }

    @Transactional
    public PostingDetailDto createPosting(CreatePostingDto dto, String username) {
        com.thesis.freelance.features.user.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Client profile not found for user"));

        Location location = null;
        if (dto.location() != null && dto.location().id() != null) {
            location = locationRepository.findById(dto.location().id()).orElse(null);
        }

        Posting posting = Posting.builder()
                .client(client)
                .title(dto.title())
                .categoryId(dto.categoryId())
                .photoUrl(dto.photoUrl())
                .budget(dto.budget())
                .deadline(dto.deadline())
                .description(dto.description())
                .status(dto.status())
                .location(location)
                .experienceLevel(dto.experienceLevel())
                .viewsCount(0L)
                .attachmentUrls(dto.attachmentUrls() != null ? dto.attachmentUrls() : new ArrayList<String>())
                .build();

        Posting savedPosting = postingRepository.save(posting);

        if (dto.skillIds() != null && !dto.skillIds().isEmpty()) {
            saveSkills(savedPosting, dto.skillIds());
        }

        return postingToDetailDtoConverter.convert(
                postingRepository.findByIdWithClientAndLocation(ActivityStatus.ACTIVE, savedPosting.getId())
        );
    }
    
    @Transactional
    public PostingDetailDto updatePosting(UUID id, UpdatePostingDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Posting posting = postingRepository.findByIdWithClientAndLocation(ActivityStatus.ACTIVE, id);
        if (posting == null) {
            throw new PostingNotFoundException(id);
        }
        
        if (!posting.getClient().getUserId().equals(user.getId())) {
            throw new UnauthorizedPostingAccessException(id);
        }
        
        if (dto.title() != null) {
            posting.setTitle(dto.title());
        }
        if (dto.categoryId() != null) {
            posting.setCategoryId(dto.categoryId());
        }
        if (dto.photoUrl() != null) {
            posting.setPhotoUrl(dto.photoUrl());
        }
        else {
            posting.setPhotoUrl("https://img.pikbest.com/backgrounds/20190904/complex-digital-work-background-v_1546977jpg!w700wp");
        }
        if (dto.budget() != null) {
            posting.setBudget(dto.budget());
        }
        if (dto.deadline() != null) {
            posting.setDeadline(dto.deadline());
        }
        if (dto.description() != null) {
            posting.setDescription(dto.description());
        }
        if (dto.status() != null) {
            posting.setStatus(dto.status());
        }
        if (dto.experienceLevel() != null) {
            posting.setExperienceLevel(dto.experienceLevel());
        }
        if (dto.location() != null && dto.location().id() != null) {
            Location location = locationRepository.findById(dto.location().id()).orElse(null);
            posting.setLocation(location);
        }
        if (dto.attachmentUrls() != null) {
            posting.setAttachmentUrls(dto.attachmentUrls());
        }
        
        Posting savedPosting = postingRepository.save(posting);
        
        if (dto.skillIds() != null) {
            postingSkillsRepository.deleteByPostingId(id);
            if (!dto.skillIds().isEmpty()) {
                saveSkills(savedPosting, dto.skillIds());
            }
        }
        
        return postingToDetailDtoConverter.convert(
                postingRepository.findByIdWithClientAndLocation(ActivityStatus.ACTIVE, savedPosting.getId())
        );
    }
    
    @Transactional
    public void deletePosting(UUID id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Posting posting = postingRepository.findById(id)
                .orElseThrow(() -> new PostingNotFoundException(id));
        
        if (!posting.getClient().getUserId().equals(user.getId())) {
            throw new UnauthorizedPostingAccessException(id);
        }
        
        postingSkillsRepository.deleteByPostingId(id);
        postingRepository.delete(posting);
    }
    
    @Transactional
    public void incrementViews(UUID id) {
        Posting posting = postingRepository.findById(id)
                .orElseThrow(() -> new PostingNotFoundException(id));
        posting.incrementViews();
        postingRepository.save(posting);
    }
    
    @Transactional
    public PostingDetailDto updateStatus(UUID id, PostingStatus status, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Posting posting = postingRepository.findById(id)
                .orElseThrow(() -> new PostingNotFoundException(id));
        
        if (!posting.getClient().getUserId().equals(user.getId())) {
            throw new UnauthorizedPostingAccessException(id);
        }
        
        posting.setStatus(status);
        Posting savedPosting = postingRepository.save(posting);
        
        return postingToDetailDtoConverter.convert(
                postingRepository.findByIdWithClientAndLocation(ActivityStatus.ACTIVE, savedPosting.getId())
        );
    }
    
    private void saveSkills(Posting posting, List<UUID> skillIds) {
        for (UUID skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId).orElse(null);
            if (skill != null) {
                PostingSkills postingSkill = PostingSkills.builder()
                        .posting(posting)
                        .skill(skill)
                        .build();
                postingSkillsRepository.save(postingSkill);
            }
        }

        
    }
}
