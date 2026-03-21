package com.thesis.freelance.features.certification.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {

    @Query("SELECT c FROM Certification c WHERE c.freelancer.id = :freelancerId AND c.activityStatus = :activityStatus ORDER BY c.date DESC")
    List<Certification> findAllByFreelancer_IdOrderByDateDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT c FROM Certification c WHERE c.freelancer.userId = :userId AND c.activityStatus = :activityStatus AND (c.expiration IS NULL OR c.expiration > CURRENT_DATE)")
    List<Certification> findAllByFreelancerUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT c FROM Certification c WHERE c.freelancer.userId = :userId")
    List<Certification> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);

    @Query("SELECT c FROM Certification c WHERE c.activityStatus = :status AND c.expiration IS NOT NULL AND c.expiration < :today")
    List<Certification> findExpiredActiveCertifications(@Param("status") ActivityStatus status, @Param("today") LocalDate today);
}
