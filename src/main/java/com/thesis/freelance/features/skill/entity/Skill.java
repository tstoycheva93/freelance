package com.thesis.freelance.features.skill.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.SkillType;
import com.thesis.freelance.features.category.entity.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "skill",
        indexes = {
                @Index(name = "idx_skill_name", columnList = "skill"),
                @Index(name = "idx_skill_category", columnList = "category_id"),
                @Index(name = "idx_skill_popularity", columnList = "popularity"),
                @Index(name = "idx_skill_type", columnList = "type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_skill_name", columnNames = "skill")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//TODO fix type setting

public class Skill extends BaseEntity {

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 255, message = "Skill name must be between 2 and 255 characters")
    @Column(name = "skill", nullable = false, unique = true, length = 255)
    private String skill;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_skill_category")
    )
    private Category category;

    @Column(name = "popularity", nullable = false)
    @Min(0)
    @Builder.Default
    private Long popularity = 0L;

    @Column(name = "aliases", columnDefinition = "JSON")
    private String aliases;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SkillType type = SkillType.TECHNICAL;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public void incrementPopularity() {
        this.popularity++;
    }

    @Transient
    public boolean isPopular() {
        return this.popularity > 100;
    }
}
