package com.thesis.freelance.features.client.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "client",
        indexes = {
                @Index(name = "idx_client_user_id", columnList = "user_id"),
                @Index(name = "idx_client_rating", columnList = "rating")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_user_id", columnNames = {"user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Size(max = 255)
    @Column(length = 255)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String website;

    @Column(name = "postings_count", nullable = false)
    @Builder.Default
    private Long postingsCount = 0L;

    @Column(name = "reviews_count", nullable = false)
    @Builder.Default
    private Long reviewsCount = 0L;

    @Column(nullable = false, precision = 3, scale = 2)
    @Min(0)
    @Max(5)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "completeness", nullable = false)
    @Builder.Default
    private Integer completeness = 0;

    // Helper methods

    public void incrementPostings() {
        this.postingsCount++;
    }

    public void incrementReviews() {
        this.reviewsCount++;
    }

    public void updateRating(BigDecimal newRating) {
        this.rating = newRating;
    }

    public void updateCompleteness(Integer percentage) {
        this.completeness = Math.min(100, Math.max(0, percentage));
    }

    @Transient
    public boolean hasCompanyProfile() {
        return this.companyName != null && !this.companyName.trim().isEmpty();
    }

    @Transient
    public boolean isProfileComplete() {
        return this.completeness >= 100;
    }

}