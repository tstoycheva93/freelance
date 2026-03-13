package com.thesis.freelance.features.uploadedCV.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UploadedCVRepository extends JpaRepository<UploadedCV, UUID> {

    @Query("SELECT u FROM UploadedCV u WHERE u.freelancer.id = :freelancerId AND u.activityStatus = :activityStatus ORDER BY u.uploadedAt DESC")
    List<UploadedCV> findAllByFreelancer_IdOrderByUploadedAtDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT u FROM UploadedCV u WHERE u.freelancer.id = :freelancerId AND u.activityStatus = :activityStatus ORDER BY u.uploadedAt DESC")
    Optional<UploadedCV> findFirstByFreelancer_IdOrderByUploadedAtDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT u FROM UploadedCV u WHERE u.freelancer.userId = :userId AND u.activityStatus = :activityStatus")
    List<UploadedCV> findAllByFreelancerUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT u FROM UploadedCV u WHERE u.freelancer.userId = :userId")
    List<UploadedCV> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);
}

