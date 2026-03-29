package com.thesis.freelance.features.offer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "offer_receipt")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferReceipt extends BaseEntity {

    @Column(name = "offer_id", nullable = false, unique = true)
    private UUID offerId;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Lob
    @Column(name = "receipt_pdf", columnDefinition = "LONGBLOB")
    private byte[] receiptPdf;

    @Column(name = "settled_amount", precision = 8, scale = 2)
    private BigDecimal settledAmount;
}
