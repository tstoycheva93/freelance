package com.thesis.freelance.features.system.repository;

import com.thesis.freelance.features.system.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, UUID> {

    @Query("SELECT s FROM SystemConfig s ORDER BY s.id")
    Optional<SystemConfig> findSingle();
}
