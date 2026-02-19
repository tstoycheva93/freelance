package com.thesis.freelance.features.message.repository;

import com.thesis.freelance.features.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
