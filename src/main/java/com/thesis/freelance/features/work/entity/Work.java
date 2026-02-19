package com.thesis.freelance.features.work.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
//TODO work type enum?
@Entity
@Table(
        name = "work",
        indexes = {
                @Index(name = "idx_work_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_work_start_date", columnList = "start_date"),
                @Index(name = "idx_work_end_date", columnList = "end_date"),
                @Index(name = "idx_work_is_current", columnList = "is_current")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Work extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "freelancer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_work_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "company", length = 255)
    private String company;

    @Column(name = "type", length = 100)
    private String type; // e.g. "full_time", "contract" etc (string is fine)

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
