package com.thesis.freelance.features.posting.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.skill.entity.Skill;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posting_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostingSkills extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Posting posting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Skill skill;

}