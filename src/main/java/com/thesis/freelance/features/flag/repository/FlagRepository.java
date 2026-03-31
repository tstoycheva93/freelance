package com.thesis.freelance.features.flag.repository;

import com.thesis.freelance.common.enums.FlagStatus;
import com.thesis.freelance.common.enums.FlagTarget;
import com.thesis.freelance.features.flag.entity.Flag;
import com.thesis.freelance.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {

    boolean existsByTargetAndTargetIdAndReporter(FlagTarget target, UUID targetId, User reporter);

    List<Flag> findByStatus(FlagStatus status);

    Page<Flag> findByStatus(FlagStatus status, Pageable pageable);

    @Query("SELECT f FROM Flag f WHERE f.target = :target AND f.targetId = :targetId")
    List<Flag> findByTargetAndTargetId(@Param("target") FlagTarget target, @Param("targetId") UUID targetId);

    Optional<Flag> findById(UUID id);
}
