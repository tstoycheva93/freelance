package com.thesis.freelance.features.conversation.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.posting.entity.Posting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "conversation",
        indexes = {
                @Index(name = "idx_conversation_posting_id", columnList = "posting_id"),
                @Index(name = "idx_conversation_client_id", columnList = "client_id"),
                @Index(name = "idx_conversation_freelancer_id", columnList = "freelancer_id"),
                @Index(name = "idx_conversation_last_msg_at", columnList = "last_msg_at")
        },
        uniqueConstraints = {
                // TODO decide if unique per posting or direct
                @UniqueConstraint(
                        name = "uk_conversation_posting_client_freelancer",
                        columnNames = {"posting_id", "client_id", "freelancer_id"}
                )
        }
)
@Getter @Setter
public class Conversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "posting_id",
            foreignKey = @ForeignKey(name = "fk_conversation_posting")
    )
    private Posting posting; // nullable = direct chat

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_conversation_client")
    )
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "freelancer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_conversation_freelancer")
    )
    private Freelancer freelancer;

    @Column(name = "last_msg_at")
    private LocalDateTime lastMsgAt;


    /* ---------- Helpers ---------- */

    @Transient
    public boolean hasMessages() {
        return lastMsgAt != null;
    }

    /** Call this when a new message is created/sent */
    public void touchLastMsgAt() {
        this.lastMsgAt = LocalDateTime.now();
    }

    /** Use this if you already know the message timestamp (recommended) */
    public void updateLastMsgAt(LocalDateTime messageSentAt) {
        if (messageSentAt == null) return;

        // only move forward
        if (this.lastMsgAt == null || messageSentAt.isAfter(this.lastMsgAt)) {
            this.lastMsgAt = messageSentAt;
        }
    }

    @Transient
    public boolean isDirectChat() {
        return posting == null;
    }

}



