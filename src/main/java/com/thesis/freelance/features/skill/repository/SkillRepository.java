package com.thesis.freelance.features.skill.repository;

import com.thesis.freelance.features.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findBySkill(String skill);

    List<Skill> findByCategory_Id(UUID categoryId);

}
