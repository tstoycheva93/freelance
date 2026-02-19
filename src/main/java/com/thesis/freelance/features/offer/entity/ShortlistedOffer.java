package com.thesis.freelance.features.offer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.client.entity.Client;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shortlisted_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortlistedOffer extends BaseEntity {
    //TODO - remove entirely and rely on flag in offer

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Offer offer;

    // createdAt from BaseEntity shows when it was shortlisted
}
