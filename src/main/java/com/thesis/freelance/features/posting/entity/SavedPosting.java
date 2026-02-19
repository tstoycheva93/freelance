package com.thesis.freelance.features.posting.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPosting extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Freelancer freelancer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Posting posting;
}