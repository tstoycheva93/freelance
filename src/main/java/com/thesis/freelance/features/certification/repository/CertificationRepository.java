package com.thesis.freelance.features.certification.repository;

import com.thesis.freelance.features.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {

    List<Certification> findAllByFreelancer_IdOrderByDateDesc(UUID freelancerId);
}
