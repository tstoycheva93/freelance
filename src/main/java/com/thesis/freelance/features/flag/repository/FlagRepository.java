package com.thesis.freelance.features.flag.repository;

import com.thesis.freelance.common.enums.FlagStatus;
import com.thesis.freelance.common.enums.FlagTarget;
import com.thesis.freelance.features.flag.entity.Flag;
import com.thesis.freelance.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {

    boolean existsByTargetAndTargetIdAndReporter(FlagTarget target, UUID targetId, User reporter);

    Page<Flag> findByStatus(FlagStatus status, Pageable pageable);

    Optional<Flag> findById(UUID id);
}
