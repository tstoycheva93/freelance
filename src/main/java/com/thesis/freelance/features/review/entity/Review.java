package com.thesis.freelance.features.review.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User reviewee;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer overallRating;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer timelinessRating;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer qualityRating;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}