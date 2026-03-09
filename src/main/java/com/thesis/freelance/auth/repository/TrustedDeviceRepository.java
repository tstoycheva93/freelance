package com.thesis.freelance.auth.repository;

import com.thesis.freelance.auth.entity.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {
    boolean existsByUserIdAndDeviceId(UUID userId, String deviceId);
    void deleteByUserIdAndDeviceId(UUID userId, String deviceId);
    List<TrustedDevice> findByUserId(UUID userId);
}
