package com.thesis.freelance.features.certification.repository;

import com.thesis.freelance.features.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {

    List<Certification> findAllByFreelancer_IdOrderByDateDesc(UUID freelancerId);

    @Query("SELECT c FROM Certification c WHERE c.freelancer.userId = :userId")
    List<Certification> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
