package com.thesis.freelance.features.freelancer.service;

import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalDto;
import com.thesis.freelance.features.freelancer.dto.FreelancerPersonalUpdateDto;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FreelancerPersonalService {

    private final FreelancerRepository freelancerRepository;

    @Transactional(readOnly = true)
    public FreelancerPersonalDto getPersonal(UUID freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));
        return toPersonalDto(freelancer);
    }

    @Transactional
    public FreelancerPersonalDto updatePersonal(UUID freelancerId, FreelancerPersonalUpdateDto dto) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new IllegalArgumentException("Freelancer not found"));

        if (dto.title() != null) freelancer.setTitle(dto.title());
        if (dto.bio() != null) freelancer.setBio(dto.bio());
        if (dto.website() != null) freelancer.setWebsite(dto.website());
        if (dto.experience() != null) freelancer.setExperience(dto.experience());
        if (dto.availability() != null) freelancer.setAvailability(dto.availability());

        freelancer.setCompleteness(calculateFreelancerCompleteness(freelancer));
        freelancerRepository.save(freelancer);

        return toPersonalDto(freelancer);
    }

    private int calculateFreelancerCompleteness(Freelancer freelancer) {
        int filled = 0;
        int total = 4;

        if (freelancer.getTitle() != null && !freelancer.getTitle().isBlank()) filled++;
        if (freelancer.getBio() != null && !freelancer.getBio().isBlank()) filled++;
        if (freelancer.getWebsite() != null && !freelancer.getWebsite().isBlank()) filled++;
        if (freelancer.getExperience() != null) filled++;

        return (filled * 100) / total;
    }

    private FreelancerPersonalDto toPersonalDto(Freelancer freelancer) {
        return new FreelancerPersonalDto(
                freelancer.getId(),
                freelancer.getTitle(),
                freelancer.getBio(),
                freelancer.getWebsite(),
                freelancer.getExperience(),
                freelancer.getAvailability(),
                freelancer.getCompleteness()
        );
    }
}
