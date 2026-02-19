package com.thesis.freelance.features.freelancer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.skill.entity.Skill;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "freelancer_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreelancerSkills extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Freelancer freelancer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Skill skill;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer level;
}