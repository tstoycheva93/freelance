package com.thesis.freelance.features.freelancer.repository;

import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FreelancerSkillsRepository extends JpaRepository<FreelancerSkills, UUID> {
    List<FreelancerSkills> findByFreelancerId(UUID freelancerId);
}
