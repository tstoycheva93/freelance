package com.thesis.freelance.features.posting.repository;

import com.thesis.freelance.features.posting.entity.SavedPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedPostingRepository extends JpaRepository<SavedPosting, UUID> {
    List<SavedPosting> findByFreelancerId(UUID freelancerId);
    boolean existsByFreelancerIdAndPostingId(UUID freelancerId, UUID postingId);
    void deleteByFreelancerIdAndPostingId(UUID freelancerId, UUID postingId);

    @Query("SELECT s FROM SavedPosting s WHERE s.freelancer.userId = :userId")
    List<SavedPosting> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
