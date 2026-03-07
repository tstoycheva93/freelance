package com.thesis.freelance.features.user.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole extends BaseEntity {

    @Column(name = "role", nullable = false, unique = true, length = 50)
    private String role;

    @Transient
    public String getRole() {
    return role;
    }
}