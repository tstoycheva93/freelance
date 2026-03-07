package com.thesis.freelance.features.offer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.posting.entity.Posting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "offer",
        indexes = {
                @Index(name = "idx_offer_posting_id", columnList = "posting_id"),
                @Index(name = "idx_offer_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_offer_status", columnList = "status"),
                @Index(name = "idx_offer_submitted_at", columnList = "submitted_at"),
                @Index(name = "idx_offer_posting_status", columnList = "posting_id, status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_offer_posting_freelancer", columnNames = {"posting_id", "freelancer_id"})
        }
)
@Getter
@Setter
public class Offer extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "posting_id",
            foreignKey = @ForeignKey(name = "fk_offer_posting")
    )
    private Posting posting;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "freelancer_id",
            foreignKey = @ForeignKey(name = "fk_offer_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "cover_letter", columnDefinition = "TEXT", nullable = false)
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferStatus status;

    @Column(name = "delivery_time", nullable = false)
    private LocalDate deliveryTime;

    @Column(name = "message", columnDefinition = "TEXT",nullable = true)
    private String message;


    // Helper methods
    public void markAsViewed() {
        if (this.viewedAt == null) {
            this.viewedAt = LocalDateTime.now();
        }
    }

    public void shortlist() {
        this.status = OfferStatus.SHORTLISTED;
    }

    public void accept() {
        this.status = OfferStatus.ACCEPTED;
    }

    public void reject() {
        this.status = OfferStatus.REJECTED;
    }

    public void cancel() {
        this.status = OfferStatus.CANCELED;
    }

    //<-------------------- ATTACHMENTS ----------------------->

    @ElementCollection
    @CollectionTable(
            name = "offers_attachments",
            joinColumns = @JoinColumn(name = "offer_id")
    )
    @Column(name = "attachment_url", length = 255)
    private List<String> attachments = new ArrayList<>();

    public void addAttachment(String url) {
        this.attachments.add(url);
    }

    public void removeAttachment(String url) {
        this.attachments.remove(url);
    }
}