package com.thesis.freelance.features.posting.repository;

import com.thesis.freelance.features.posting.entity.PostingSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostingSkillsRepository extends JpaRepository<PostingSkills, UUID> {
    List<PostingSkills> findByPostingId(UUID postingId);

    @Query("SELECT ps.skill.skill FROM PostingSkills ps WHERE ps.posting.id = :postingId")
    List<String> findSkillNamesByPostingId(@Param("postingId") UUID postingId);

    @Query("SELECT ps.skill.id FROM PostingSkills ps WHERE ps.posting.id = :postingId")
    List<String> findSkillIdsByPostingId(@Param("postingId") UUID postingId);

    @Modifying
    @Query("DELETE FROM PostingSkills ps WHERE ps.posting.id = :postingId")
    void deleteByPostingId(@Param("postingId") UUID postingId);
}