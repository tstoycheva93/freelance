package com.thesis.freelance.features.work.repository;


import com.thesis.freelance.features.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkRepository extends JpaRepository<Work, UUID> {

    List<Work> findAllByFreelancer_IdOrderByIsCurrentDescStartDateDesc(UUID freelancerId);
}
