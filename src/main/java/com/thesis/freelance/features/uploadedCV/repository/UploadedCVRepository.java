package com.thesis.freelance.features.uploadedCV.repository;

import com.thesis.freelance.features.uploadedCV.entity.UploadedCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UploadedCVRepository extends JpaRepository<UploadedCV, UUID> {

    List<UploadedCV> findAllByFreelancer_IdOrderByUploadedAtDesc(UUID freelancerId);

    Optional<UploadedCV> findFirstByFreelancer_IdOrderByUploadedAtDesc(UUID freelancerId);

    @Query("SELECT u FROM UploadedCV u WHERE u.freelancer.userId = :userId")
    List<UploadedCV> findAllByFreelancerUserId(@Param("userId") UUID userId);
}

