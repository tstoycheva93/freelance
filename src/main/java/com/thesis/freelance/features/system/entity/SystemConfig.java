package com.thesis.freelance.features.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    private UUID id;

    @Column(name = "registration_enabled", nullable = false)
    private boolean registrationEnabled = true;

    @Column(name = "registration_message", columnDefinition = "TEXT")
    private String registrationMessage;

    @Column(name = "maintenance_mode", nullable = false)
    private boolean maintenanceMode = false;

    @Column(name = "maintenance_message", columnDefinition = "TEXT")
    private String maintenanceMessage;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
