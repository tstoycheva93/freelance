package com.thesis.freelance.features.posting.service;

import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.entity.SavedPosting;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.posting.repository.SavedPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedPostingService {

    private final SavedPostingRepository savedPostingRepository;
    private final FreelancerRepository freelancerRepository;
    private final PostingRepository postingRepository;

    @Transactional(readOnly = true)
    public boolean isSavedByFreelancer(UUID userId, UUID postingId) {
        Freelancer freelancer = freelancerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        return savedPostingRepository.existsByFreelancerIdAndPostingId(freelancer.getId(), postingId);
    }

    @Transactional
    public SavedPosting savePosting(UUID userId, UUID postingId) {
        Freelancer freelancer = freelancerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        if (savedPostingRepository.existsByFreelancerIdAndPostingId(freelancer.getId(), postingId)) {
            return null; // Already saved
        }

        Posting posting = postingRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("Posting not found"));

        SavedPosting savedPosting = SavedPosting.builder()
                .freelancer(freelancer)
                .posting(posting)
                .build();

        return savedPostingRepository.save(savedPosting);
    }

    @Transactional
    public void unsavePosting(UUID userId, UUID postingId) {
        Freelancer freelancer = freelancerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        savedPostingRepository.deleteByFreelancerIdAndPostingId(freelancer.getId(), postingId);
    }

    @Transactional
    public SavedPosting toggleSave(UUID userId, UUID postingId) {
        Freelancer freelancer = freelancerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        boolean isSaved = savedPostingRepository.existsByFreelancerIdAndPostingId(freelancer.getId(), postingId);
        if (isSaved) {
            unsavePosting(userId, postingId);
            return null;
        } else {
            return savePosting(userId, postingId);
        }
    }
}
