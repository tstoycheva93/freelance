package com.thesis.freelance.features.conversation.repository;

import com.thesis.freelance.features.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    // For posting-based conversation
    Optional<Conversation> findByPosting_IdAndClient_IdAndFreelancer_Id(UUID postingId, UUID clientId, UUID freelancerId);

    // For direct chat (posting is null)
    Optional<Conversation> findByPostingIsNullAndClient_IdAndFreelancer_Id(UUID clientId, UUID freelancerId);

    // List all conversations for a client
    List<Conversation> findAllByClient_IdOrderByLastMsgAtDesc(UUID clientId);

    // List all conversations for a freelancer
    List<Conversation> findAllByFreelancer_IdOrderByLastMsgAtDesc(UUID freelancerId);

    // Optional: if you want to fetch the conversation by both participants (either side)
    List<Conversation> findAllByClient_IdAndFreelancer_IdOrderByLastMsgAtDesc(UUID clientId, UUID freelancerId);
}
