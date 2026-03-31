package com.thesis.freelance.features.flag.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.FlagStatus;
import com.thesis.freelance.common.enums.FlagTarget;
import com.thesis.freelance.common.enums.FlagType;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "flag",
    indexes = {
        @Index(name = "idx_flag_status", columnList = "status"),
        @Index(name = "idx_flag_target", columnList = "target"),
        @Index(name = "idx_flag_reporter", columnList = "reporter_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_flag_reporter_target", columnNames = {"reporter_id", "target", "target_id"})
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flag extends BaseEntity {

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FlagType type;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FlagStatus status;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FlagTarget target;

    @Column(name = "target_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID targetId;

    @Column(name = "reason", columnDefinition = "TEXT",nullable = true)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = true)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "review_time", nullable = true)
    private LocalDate reviewTime;

}