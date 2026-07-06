package com.thesis.freelance.features.message.repository;

import com.thesis.freelance.features.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByConversation_IdOrderBySentAtAsc(UUID conversationId);

    Optional<Message> findFirstByConversation_IdOrderBySentAtDesc(UUID conversationId);

    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Message> findAllByUserIdNoFilter(@Param("userId") UUID userId);

    boolean existsByClientMessageId(UUID clientMessageId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :convId AND m.receiver.id = :userId AND m.isRead = false")
    int markAsReadByConversationAndReceiver(@Param("convId") UUID conversationId, @Param("userId") UUID userId);

    List<Message> findByConversation_IdAndReceiver_IdAndIsReadFalse(UUID conversationId, UUID receiverId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.conversation.id = :conversationId")
    void deleteByConversation_Id(@Param("conversationId") UUID conversationId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.conversation.offer.id = :offerId")
    void deleteByOfferId(@Param("offerId") UUID offerId);
}
