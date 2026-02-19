package com.thesis.freelance.features.client.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, unique = true)
    private User userId;

    @Size(max = 255)
    @Column(length = 255)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String website;

    @Column(nullable = false, precision = 3, scale = 2)
    @Min(0)
    @Max(5)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Transient
    public int getPostingsCount() {
        // Will be calculated by repository query
        return 0;
    }

    @Transient
    public int getReviewsCount() {
        // Will be calculated by repository query
        return 0;
    }

    @Transient
    public boolean hasCompanyProfile() {
        return this.companyName != null && !this.companyName.trim().isEmpty();
    }

}