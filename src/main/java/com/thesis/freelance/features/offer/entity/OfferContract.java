package com.thesis.freelance.features.offer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "offer_contract")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferContract extends BaseEntity {

    @Column(name = "offer_id", nullable = false, unique = true)
    private UUID offerId;

    @Column(name = "freelancer_signed", nullable = false)
    private boolean freelancerSigned = false;

    @Column(name = "client_signed", nullable = false)
    private boolean clientSigned = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Lob
    @Column(name = "contract_pdf", columnDefinition = "LONGBLOB")
    private byte[] contractPdf;
}
