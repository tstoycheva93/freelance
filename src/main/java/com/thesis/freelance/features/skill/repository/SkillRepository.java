package com.thesis.freelance.features.skill.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findBySkill(String skill);

    List<Skill> findByCategory_Id(UUID categoryId);

    @Query("SELECT s FROM Skill s WHERE s.activityStatus = :activityStatus")
    List<Skill> findAllByActivityStatus(@Param("activityStatus") ActivityStatus activityStatus);
}
