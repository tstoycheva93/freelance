package com.thesis.freelance.features.conversation.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // For posting-based conversation
    Optional<Conversation> findByPosting_IdAndClient_IdAndFreelancer_Id(UUID postingId, UUID clientId, UUID freelancerId);

    // For direct chat (posting is null)
    Optional<Conversation> findByPostingIsNullAndClient_IdAndFreelancer_Id(UUID clientId, UUID freelancerId);

    // List all conversations for a client
    @Query("SELECT c FROM Conversation c WHERE c.client.id = :clientId AND c.activityStatus = :activityStatus")
    List<Conversation> findAllByClient_IdOrderByLastMsgAtDesc(@Param("clientId") UUID clientId, @Param("activityStatus") ActivityStatus activityStatus);

    // List all conversations for a freelancer
    @Query("SELECT c FROM Conversation c WHERE c.freelancer.id = :freelancerId AND c.activityStatus = :activityStatus")
    List<Conversation> findAllByFreelancer_IdOrderByLastMsgAtDesc(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    // Optional: if you want to fetch the conversation by both participants (either side)
    List<Conversation> findAllByClient_IdAndFreelancer_IdOrderByLastMsgAtDesc(UUID clientId, UUID freelancerId);

    @Query("SELECT c FROM Conversation c WHERE (c.client.userId = :userId OR c.freelancer.userId = :userId) AND c.activityStatus = :activityStatus")
    List<Conversation> findAllByUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT c FROM Conversation c WHERE c.client.userId = :userId OR c.freelancer.userId = :userId")
    List<Conversation> findAllByUserIdNoFilter(@Param("userId") UUID userId);
}
