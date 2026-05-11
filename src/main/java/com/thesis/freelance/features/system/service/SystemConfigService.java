package com.thesis.freelance.features.system.service;

import com.thesis.freelance.features.system.entity.SystemConfig;
import com.thesis.freelance.features.system.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private static final UUID CONFIG_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final SystemConfigRepository systemConfigRepository;

    @Cacheable("systemConfig")
    public SystemConfig getConfig() {
        return systemConfigRepository.findById(CONFIG_ID)
                .orElseGet(this::createDefault);
    }

    @CacheEvict(value = "systemConfig", allEntries = true)
    @Transactional
    public SystemConfig updateRegistration(boolean enabled, String message, String updatedBy) {
        SystemConfig config = getConfig();
        config.setRegistrationEnabled(enabled);
        config.setRegistrationMessage(message);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(LocalDateTime.now());
        return systemConfigRepository.save(config);
    }

    @CacheEvict(value = "systemConfig", allEntries = true)
    @Transactional
    public SystemConfig toggleMaintenance(boolean enabled, String message, String updatedBy) {
        SystemConfig config = getConfig();
        config.setMaintenanceMode(enabled);
        config.setMaintenanceMessage(message);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(LocalDateTime.now());
        return systemConfigRepository.save(config);
    }

    public boolean isMaintenanceMode() {
        return getConfig().isMaintenanceMode();
    }

    public boolean isRegistrationEnabled() {
        return getConfig().isRegistrationEnabled();
    }

    private SystemConfig createDefault() {
        SystemConfig config = SystemConfig.builder()
                .id(CONFIG_ID)
                .registrationEnabled(true)
                .maintenanceMode(false)
                .updatedAt(LocalDateTime.now())
                .updatedBy("system")
                .build();
        return systemConfigRepository.save(config);
    }
}
