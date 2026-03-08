package com.thesis.freelance.features.freelancer.repository;

import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FreelancerSkillsRepository extends JpaRepository<FreelancerSkills, UUID> {
    List<FreelancerSkills> findByFreelancerId(UUID freelancerId);
    Optional<FreelancerSkills> findByFreelancerAndSkill(Freelancer freelancer, Skill skill);
    @Query("""
    select fs
    from FreelancerSkills fs
    join fetch fs.skill
    where fs.freelancer.id = :freelancerId
""")
    List<FreelancerSkills> findByFreelancerIdWithSkill(@Param("freelancerId") UUID freelancerId);

    @Query("SELECT fs FROM FreelancerSkills fs WHERE fs.freelancer.userId = :userId")
    List<FreelancerSkills> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
