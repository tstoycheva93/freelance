package com.thesis.freelance.features.certification.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "certification",
        indexes = {
                @Index(name = "idx_certification_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_certification_date", columnList = "date"),
                @Index(name = "idx_certification_expiration", columnList = "expiration")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "freelancer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_certification_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "organization", length = 255)
    private String organization;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "expiration")
    private LocalDate expiration;

    @Column(name = "identifier", length = 255)
    private String identifier;
}
