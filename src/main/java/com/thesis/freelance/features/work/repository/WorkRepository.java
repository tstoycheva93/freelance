package com.thesis.freelance.features.work.repository;


import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkRepository extends JpaRepository<Work, UUID> {

    @Query("SELECT w FROM Work w WHERE w.freelancer.id = :freelancerId AND w.activityStatus = :activityStatus ORDER BY w.isCurrent DESC, w.startDate DESC")
    List<Work> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT w FROM Work w WHERE w.freelancer.userId = :userId AND w.activityStatus = :activityStatus")
    List<Work> findAllByFreelancerUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT w FROM Work w WHERE w.freelancer.userId = :userId")
    List<Work> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);
}
