package com.thesis.freelance.features.message.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import com.thesis.freelance.features.conversation.entity.Conversation;
import com.thesis.freelance.features.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "message",
        indexes = {
                @Index(name = "idx_message_conversation_id", columnList = "conversation_id"),
                @Index(name = "idx_message_sender_id", columnList = "sender_id"),
                @Index(name = "idx_message_receiver_id", columnList = "receiver_id"),
                @Index(name = "idx_message_sent_at", columnList = "sent_at"),
                @Index(name = "idx_message_conversation_sent", columnList = "conversation_id, sent_at")
        }
)
@Getter
@Setter
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "conversation_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_conversation")
    )
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "sender_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_sender")
    )
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "receiver_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_message_receiver")
    )
    private User receiver;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    /* ---------- Helpers ---------- */

    @Transient
    public boolean isSentBy(User user) {
        return user != null && sender != null && sender.getId() != null
                && sender.getId().equals(user.getId());
    }
}

/*
For saving a new message:
messageRepository.save(message);
conversation.updateLastMsgAt(message.getSentAt());
conversationRepository.save(conversation);
*/