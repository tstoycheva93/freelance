package com.thesis.freelance.features.education.repository;


import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    @Query("SELECT e FROM Education e WHERE e.freelancer.id = :freelancerId AND e.activityStatus = :activityStatus ORDER BY e.isCurrent DESC, e.startDate DESC")
    List<Education> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT e FROM Education e WHERE e.freelancer.userId = :userId AND e.activityStatus = :activityStatus")
    List<Education> findAllByFreelancerUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT e FROM Education e WHERE e.freelancer.userId = :userId")
    List<Education> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);
}
