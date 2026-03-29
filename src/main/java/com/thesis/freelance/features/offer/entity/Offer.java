package com.thesis.freelance.features.offer.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.posting.entity.Posting;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "offer",
        indexes = {
                @Index(name = "idx_offer_posting_id", columnList = "posting_id"),
                @Index(name = "idx_offer_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_offer_status", columnList = "status"),
                @Index(name = "idx_offer_posting_status", columnList = "posting_id, status")
        })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "price", precision = 8, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferStatus status;

    @Column(name = "delivery_time", nullable = false)
    private LocalDate deliveryTime;

    @Column(name = "message", columnDefinition = "TEXT",nullable = true)
    private String message;


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

    public void markDelivered() {
        this.status = OfferStatus.DELIVERED;
        this.deliverableVisible = true;
        this.deliveredAt = LocalDateTime.now();
    }

    //<-------------------- ATTACHMENTS ----------------------->

    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
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

    //<-------------------- DELIVERABLES ----------------------->

    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "offer_deliverables", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(name = "file_url", length = 500)
    private List<String> deliverableUrls = new ArrayList<>();

    @Column(name = "deliverable_visible", nullable = false)
    private boolean deliverableVisible = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public void addDeliverable(String url) {
        this.deliverableUrls.add(url);
    }
}