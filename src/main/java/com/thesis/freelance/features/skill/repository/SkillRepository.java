package com.thesis.freelance.features.skill.repository;

import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.common.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findBySkill(String skill);

    List<Skill> findByCategoryId(UUID categoryId);

    List<Skill> findByType(SkillType type);

    List<Skill> findByIsActiveTrueOrderByPopularityDesc();

    @Query("SELECT s FROM Skill s WHERE s.isActive = true AND s.category.id = :categoryId ORDER BY s.popularity DESC")
    List<Skill> findActiveByCategoryOrderByPopularity(@Param("categoryId") UUID categoryId);

    @Query("SELECT s FROM Skill s WHERE s.isActive = true ORDER BY s.popularity DESC")
    List<Skill> findTopSkillsByPopularity();

    @Query("SELECT s FROM Skill s WHERE LOWER(s.skill) LIKE LOWER(CONCAT('%', :search, '%')) AND s.isActive = true")
    List<Skill> searchByName(@Param("search") String search);
}