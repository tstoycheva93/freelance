package com.thesis.freelance.features.posting.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.entity.SavedPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedPostingRepository extends JpaRepository<SavedPosting, UUID> {
    boolean existsByFreelancerIdAndPostingId(UUID freelancerId, UUID postingId);

    @Query("SELECT s FROM SavedPosting s WHERE s.freelancer.userId = :userId")
    List<SavedPosting> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    void deleteByFreelancerIdAndPostingId(UUID freelancerId, UUID postingId);

    @Query("SELECT s.posting FROM SavedPosting s WHERE s.freelancer.userId = :userId AND s.posting.activityStatus = :activityStatus")
    Page<Posting> findSavedPostingsByUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Modifying
    @Transactional
    void deleteByPostingId(UUID postingId);
}
