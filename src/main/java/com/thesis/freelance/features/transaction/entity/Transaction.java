package com.thesis.freelance.features.transaction.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.TransactionStatus;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User freelancer;

    @Column(nullable = false, precision = 10, scale = 2)
    @Min(0)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    private LocalDateTime completedAt;

    @Column(length = 255)
    private String note;

    @Transient
    public boolean isCompleted() {
        return TransactionStatus.COMPLETED.equals(this.status);
    }
}