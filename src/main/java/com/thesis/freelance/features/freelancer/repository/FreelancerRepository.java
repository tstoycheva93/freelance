package com.thesis.freelance.features.freelancer.repository;

import com.thesis.freelance.features.freelancer.entity.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> {
    Optional<Freelancer> findByUserId(UUID userId);
}
