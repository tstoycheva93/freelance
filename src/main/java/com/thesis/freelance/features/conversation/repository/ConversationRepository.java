package com.thesis.freelance.features.conversation.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE c.client.userId = :userId OR c.freelancer.userId = :userId")
    List<Conversation> findAllByUserIdNoFilter(@Param("userId") UUID userId);

    @Query("SELECT c FROM Conversation c WHERE (c.client.userId = :userId OR c.freelancer.userId = :userId) AND c.activityStatus = :status")
    List<Conversation> findAllByUserId(@Param("userId") UUID userId, @Param("status") ActivityStatus status);

    @Query("SELECT c FROM Conversation c WHERE c.client.id = :clientId AND c.freelancer.id = :freelancerId AND c.offer.id = :offerId AND c.activityStatus = :status")
    Optional<Conversation> findExistingConversation(
            @Param("clientId") UUID clientId,
            @Param("freelancerId") UUID freelancerId,
            @Param("offerId") UUID offerId,
            @Param("status") ActivityStatus status);

    @Query("SELECT c FROM Conversation c WHERE c.client.id = :clientId AND c.freelancer.id = :freelancerId AND c.offer.id = :offerId AND c.activityStatus = 'DELETED'")
    Optional<Conversation> findDeletedConversation(
            @Param("clientId") UUID clientId,
            @Param("freelancerId") UUID freelancerId,
            @Param("offerId") UUID offerId);

    @Modifying
    @Query("DELETE FROM Conversation c WHERE c.id = :id")
    void hardDeleteById(@Param("id") UUID id);

    void deleteByOfferId(UUID offerId);
}
