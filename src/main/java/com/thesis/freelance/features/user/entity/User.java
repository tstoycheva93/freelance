package com.thesis.freelance.features.user.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.location.entity.Location;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "`user`",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_status", columnList = "activity_status"),
                @Index(name = "idx_user_role", columnList = "user_role"),
                @Index(name = "idx_user_location", columnList = "location_id"),
                @Index(name = "idx_user_rating", columnList = "rating")
        }
)
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "user_role",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_role")
    )
    private UserRole userRole;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "photo_url", length = 500)
    @Builder.Default
    private String photoUrl = "https://ucarecdn.com/d34a1825-07de-4826-bc6d-0291c28b963c/-/scale_crop/300x300/";

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "read_on", nullable = false)
    @Builder.Default
    private Boolean readOn = true;

    @Column(name = "emails_on", nullable = false)
    @Builder.Default
    private Boolean emailsOn = true;

    @Column(name = "push_on", nullable = false)
    @Builder.Default
    private Boolean pushOn = true;

    @Column(name = "is_2fa_enabled", nullable = false)
    @Builder.Default
    private Boolean is2faEnabled = false;

    @Column(name = "rating", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "reviews_count")
    @Builder.Default
    private Long reviewsCount = 0L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "location_id",
            foreignKey = @ForeignKey(name = "fk_user_location")
    )
    private Location location;

    /* ---------- Domain helpers ---------- */

    @Transient
    public boolean hasRole(String roleName) {
        return userRole != null
                && userRole.getRole() != null
                && userRole.getRole().equalsIgnoreCase(roleName);
    }

    @Transient
    public boolean isClient() {
        return hasRole("CLIENT");
    }

    @Transient
    public boolean isFreelancer() {
        return hasRole("FREELANCER");
    }

    @Transient
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    @Transient
    public String getRoleName() {
        return userRole != null ? userRole.getRole() : null;
    }

    @Transient
    public String getFullName() {
        if (name == null || name.isBlank()) return surname;
        if (surname == null || surname.isBlank()) return name;
        return name + " " + surname;
    }
}
