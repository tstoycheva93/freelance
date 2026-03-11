package com.thesis.freelance.features.freelancer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.Availability;
import com.thesis.freelance.common.enums.ExperienceLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "freelancer",
        indexes = {
                @Index(name = "idx_freelancer_user_id", columnList = "user_id"),
                @Index(name = "idx_freelancer_availability", columnList = "availability"),
                @Index(name = "idx_freelancer_experience", columnList = "experience"),
                @Index(name = "idx_freelancer_rating", columnList = "rating"),
                @Index(name = "idx_freelancer_last_active", columnList = "last_active_at"),
                @Index(name = "idx_freelancer_availability_rating", columnList = "availability, rating")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_freelancer_user_id", columnNames = {"user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Freelancer extends BaseEntity {

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "bio", columnDefinition = "TEXT",nullable = true)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience",nullable = true)
    private ExperienceLevel experience;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability",nullable = true)
    @Builder.Default
    private Availability availability = Availability.AVAILABLE;

    @Column(name = "offers_count")
    @Builder.Default
    private Long offersCount = 0L;

    @Deprecated
    @Column(name = "reviews_count")
    @Builder.Default
    private Long reviewsCount = 0L;

    @Deprecated
    @Column(name = "rating", precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "last_active_at",nullable = true)
    private LocalDateTime lastActiveAt;

    @Column(name = "completeness", nullable = false)
    @Builder.Default
    private Integer completeness = 0;

    @Column(name = "website")
    private String website;

    // Helper methods

    public void setAvailable() {
        this.availability = Availability.AVAILABLE;
    }

    public void setBusy() {
        this.availability = Availability.BUSY;
    }

    public void setNotAvailable() {
        this.availability = Availability.NOT_AVAILABLE;
    }

    public void incrementOffers() {
        this.offersCount++;
    }

    @Deprecated
    public void incrementReviews() {
        this.reviewsCount++;
    }

    @Deprecated
    public void updateRating(BigDecimal newRating) {
        this.rating = newRating;
    }

    public void updateLastActive() {
        this.lastActiveAt = LocalDateTime.now();
    }

    public boolean isProfileComplete() {
        return completeness >= 100;
    }

    public void updateCompleteness(Integer percentage) {
        this.completeness = Math.min(100, Math.max(0, percentage));
    }
}