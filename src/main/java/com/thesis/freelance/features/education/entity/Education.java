package com.thesis.freelance.features.education.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "education",
        indexes = {
                @Index(name = "idx_education_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_education_start_date", columnList = "start_date"),
                @Index(name = "idx_education_end_date", columnList = "end_date"),
                @Index(name = "idx_education_is_current", columnList = "is_current")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "freelancer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_education_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "institution", nullable = false, length = 255)
    private String institution;

    @Column(name = "degree", length = 255)
    private String degree;

    @Column(name = "field", length = 255)
    private String field;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private boolean isCurrent = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
