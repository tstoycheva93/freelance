package com.thesis.freelance.features.education.repository;


import com.thesis.freelance.features.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(UUID freelancerId);

    @Query("SELECT e FROM Education e WHERE e.freelancer.userId = :userId")
    List<Education> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
