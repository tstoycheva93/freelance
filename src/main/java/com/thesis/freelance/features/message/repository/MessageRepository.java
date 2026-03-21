package com.thesis.freelance.features.message.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Load messages for a conversation (oldest -> newest)
    List<Message> findAllByConversation_IdOrderBySentAtAsc(UUID conversationId);

    // Load messages for a conversation (newest -> oldest)
    List<Message> findAllByConversation_IdOrderBySentAtDesc(UUID conversationId);

    // Latest message in a conversation
    Optional<Message> findFirstByConversation_IdOrderBySentAtDesc(UUID conversationId);

    // Count unread-like patterns (if you later add readAt/read flag, you'll adjust)
    long countByConversation_Id(UUID conversationId);

    // List messages between two users (across all conversations) (optional)
    List<Message> findAllBySender_IdAndReceiver_IdOrderBySentAtAsc(UUID senderId, UUID receiverId);

    // Messages in a time window (optional)
    List<Message> findAllByConversation_IdAndSentAtBetweenOrderBySentAtAsc(
            UUID conversationId,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Message> findAllByUserIdNoFilter(@Param("userId") UUID userId);

    boolean existsByClientMessageId(UUID clientMessageId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :convId AND m.receiver.id = :userId AND m.isRead = false")
    int markAsReadByConversationAndReceiver(@Param("convId") UUID conversationId, @Param("userId") UUID userId);
}
