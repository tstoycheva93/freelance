package com.thesis.freelance.features.posting.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.entity.PostingSkills;
import com.thesis.freelance.features.posting.repository.PostingSkillsRepository;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.thesis.freelance.common.enums.ActivityStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final FreelancerRepository freelancerRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final PostingSkillsRepository postingSkillsRepository;
    private final PostingRepository postingRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Integer computeMatch(UUID postingId, UUID freelancerUserId) {
        return postingRepository.findById(postingId)
                .map(posting -> computeMatchForPosting(posting, freelancerUserId))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Integer computeMatchForPosting(Posting posting, UUID freelancerUserId) {
        return freelancerRepository.findByUserId(freelancerUserId)
                .map(freelancer -> calculateMatch(posting, freelancer))
                .orElse(null);
    }

    private Integer calculateMatch(Posting posting, Freelancer freelancer) {
        List<FreelancerSkills> fSkills = freelancerSkillsRepository.findByFreelancerIdWithSkill(freelancer.getId(), ACTIVE);
        List<PostingSkills> pSkills = postingSkillsRepository.findByPostingId(posting.getId());

        User freelancerUser = userRepository.findById(freelancer.getUserId()).orElse(null);

        double skillScore = computeSkillScore(fSkills, pSkills);
        double categoryScore = computeCategoryScore(fSkills, posting.getCategoryId());
        double expScore = computeExperienceScore(freelancer, posting);
        double locationScore = computeLocationScore(freelancerUser, posting);

        double total = skillScore * 0.40 + categoryScore * 0.25 + expScore * 0.20 + locationScore * 0.15;
        return (int) Math.round(total * 100);
    }

    private double computeSkillScore(List<FreelancerSkills> fSkills, List<PostingSkills> pSkills) {
        if (pSkills.isEmpty()) {
            return 1.0;
        }
        if (fSkills.isEmpty()) {
            return 0.0;
        }

        Set<UUID> fSkillIds = fSkills.stream()
                .map(fs -> fs.getSkill().getId())
                .collect(Collectors.toSet());

        long matches = pSkills.stream()
                .filter(ps -> fSkillIds.contains(ps.getSkill().getId()))
                .count();

        return (double) matches / pSkills.size();
    }

    private double computeCategoryScore(List<FreelancerSkills> fSkills, UUID postingCategoryId) {
        if (postingCategoryId == null) {
            return 0.0;
        }

        return fSkills.stream()
                .map(FreelancerSkills::getSkill)
                .map(Skill::getCategory)
                .filter(c -> c != null)
                .anyMatch(c -> postingCategoryId.equals(c.getId())) ? 1.0 : 0.0;
    }

    private double computeExperienceScore(Freelancer freelancer, Posting posting) {
        if (freelancer.getExperience() == null || posting.getExperienceLevel() == null) {
            return 0.0;
        }
        return freelancer.getExperience() == posting.getExperienceLevel() ? 1.0 : 0.0;
    }

    private double computeLocationScore(User freelancerUser, Posting posting) {
        if (freelancerUser == null) {
            return 0.0;
        }

        var userLoc = freelancerUser.getLocation();
        var postLoc = posting.getLocation();

        if (userLoc == null || postLoc == null) {
            return 0.0;
        }

        if (userLoc.getId().equals(postLoc.getId())) {
            return 1.0;
        }

        if (userLoc.getCountry() != null && postLoc.getCountry() != null
                && userLoc.getCountry().getId().equals(postLoc.getCountry().getId())) {
            return 0.5;
        }

        return 0.0;
    }
}
