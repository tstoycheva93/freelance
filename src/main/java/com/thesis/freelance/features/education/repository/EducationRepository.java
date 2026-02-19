package com.thesis.freelance.features.education.repository;


import com.thesis.freelance.features.education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(UUID freelancerId);
}
