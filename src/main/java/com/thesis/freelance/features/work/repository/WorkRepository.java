package com.thesis.freelance.features.work.repository;


import com.thesis.freelance.features.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkRepository extends JpaRepository<Work, UUID> {

    List<Work> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(UUID freelancerId);

    @Query("SELECT w FROM Work w WHERE w.freelancer.userId = :userId")
    List<Work> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
