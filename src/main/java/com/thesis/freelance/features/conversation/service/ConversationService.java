package com.thesis.freelance.features.conversation.service;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.conversation.dto.*;
import com.thesis.freelance.features.conversation.entity.Conversation;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.message.dto.MessageDto;
import com.thesis.freelance.features.message.dto.ReadStatusUpdateDto;
import com.thesis.freelance.features.message.entity.Message;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.message.service.WebSocketService;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final WebSocketService webSocketService;

    @Transactional(readOnly = true)
    public List<ConversationListDto> getConversationsForUser(UUID userId) {
        List<Conversation> conversations = conversationRepository.findAllByUserId(userId, ActivityStatus.ACTIVE);
        
        return conversations.stream()
                .map(conv -> mapToConversationListDto(conv, userId))
                .sorted(Comparator.comparing(
                        ConversationListDto::lastMessageTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationDetailDto getConversationById(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

        if (!isUserPartOfConversation(conversation, userId)) {
            throw new SecurityException("User not authorized to view this conversation");
        }

        List<Message> messages = messageRepository.findAllByConversation_IdOrderBySentAtAsc(conversationId);
        int unreadCount = (int) messages.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().getId().equals(userId) && !m.isRead())
                .count();

        return mapToConversationDetailDto(conversation, messages, userId, unreadCount);
    }

    @Transactional
    public ConversationDetailDto createConversation(UUID currentUserId, CreateConversationRequest request) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (request.offerId() == null) {
            throw new IllegalArgumentException("Offer ID is required");
        }

        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new NoSuchElementException("Offer not found"));

        Client client = offer.getPosting().getClient();
        Freelancer freelancer = offer.getFreelancer();
        
        UUID currentUserId2 = currentUser.getId();
        if (!client.getUserId().equals(currentUserId2) && 
            !freelancer.getUserId().equals(currentUserId2)) {
            throw new SecurityException("User is not part of this offer");
        }

        Optional<Conversation> existing = conversationRepository.findExistingConversation(
                client.getId(), freelancer.getId(), offer.getId(), ActivityStatus.ACTIVE);
        if (existing.isPresent()) {
            return getConversationById(existing.get().getId(), currentUserId);
        }

        Optional<Conversation> deleted = conversationRepository.findDeletedConversation(
                client.getId(), freelancer.getId(), offer.getId());
        if (deleted.isPresent()) {
            messageRepository.deleteByConversation_Id(deleted.get().getId());
            conversationRepository.hardDeleteById(deleted.get().getId());
        }

        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setFreelancer(freelancer);
        conversation.setOffer(offer);
        conversation = conversationRepository.save(conversation);

        return mapToConversationDetailDto(conversation, new ArrayList<>(), currentUserId, 0);
    }

    @Transactional
    public void markMessagesAsRead(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

        if (!isUserPartOfConversation(conversation, userId)) {
            throw new SecurityException("User not authorized for this conversation");
        }

        List<Message> unreadMessages = messageRepository.findByConversation_IdAndReceiver_IdAndIsReadFalse(conversationId, userId);
        
        if (!unreadMessages.isEmpty()) {
            for (Message msg : unreadMessages) {
                msg.setRead(true);
            }
            messageRepository.saveAll(unreadMessages);
            
            Set<UUID> senderIds = unreadMessages.stream()
                    .map(m -> m.getSender().getId())
                    .collect(Collectors.toSet());
            
            List<UUID> messageIds = unreadMessages.stream().map(Message::getId).collect(Collectors.toList());
            ReadStatusUpdateDto readUpdate = new ReadStatusUpdateDto(
                    conversationId.toString(),
                    messageIds,
                    true
            );
            
            for (UUID senderId : senderIds) {
                webSocketService.sendMessageReadStatus(senderId, readUpdate);
            }
        }
    }

    @Transactional
    public void deleteConversation(UUID conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

        if (!isUserPartOfConversation(conversation, userId)) {
            throw new SecurityException("Not authorized to delete this conversation");
        }

        conversation.setActivityStatus(ActivityStatus.DELETED);
        conversationRepository.save(conversation);
    }

    private boolean isUserPartOfConversation(Conversation conversation, UUID userId) {
        UUID clientUserId = conversation.getClient().getUserId();
        UUID freelancerUserId = conversation.getFreelancer().getUserId();
        return userId.equals(clientUserId) || (freelancerUserId != null && userId.equals(freelancerUserId));
    }

    private ConversationListDto mapToConversationListDto(Conversation conversation, UUID currentUserId) {
        ParticipantDto participant = getOtherParticipant(conversation, currentUserId);
        
        Optional<Message> lastMessage = messageRepository.findFirstByConversation_IdOrderBySentAtDesc(conversation.getId());
        String lastMessageText = lastMessage.map(Message::getContent).orElse(null);
        var lastMessageTime = lastMessage.map(Message::getSentAt).orElse(null);
        
        int unreadCount = lastMessage.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().getId().equals(currentUserId) && !m.isRead())
                .mapToInt(m -> 1)
                .sum();

        List<Message> allMessages = messageRepository.findAllByConversation_IdOrderBySentAtAsc(conversation.getId());
        int totalUnread = (int) allMessages.stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().getId().equals(currentUserId) && !m.isRead())
                .count();

        RelatedOfferDto relatedOffer = null;
        if (conversation.getOffer() != null && conversation.getOffer().getPosting() != null) {
            relatedOffer = new RelatedOfferDto(
                    conversation.getOffer().getId(),
                    conversation.getOffer().getPosting().getTitle()
            );
        }

        return new ConversationListDto(
                conversation.getId(),
                participant,
                lastMessageText,
                lastMessageTime,
                totalUnread,
                relatedOffer
        );
    }

    private ConversationDetailDto mapToConversationDetailDto(Conversation conversation, List<Message> messages, 
                                                            UUID currentUserId, int unreadCount) {
        ParticipantDto participant = getOtherParticipant(conversation, currentUserId);

        RelatedOfferDto relatedOffer = null;
        if (conversation.getOffer() != null && conversation.getOffer().getPosting() != null) {
            relatedOffer = new RelatedOfferDto(
                    conversation.getOffer().getId(),
                    conversation.getOffer().getPosting().getTitle()
            );
        }

        List<MessageDto> messageDtos = messages.stream()
                .map(m -> mapToMessageDto(m, currentUserId))
                .collect(Collectors.toList());

        return new ConversationDetailDto(
                conversation.getId(),
                participant,
                relatedOffer,
                messageDtos,
                unreadCount
        );
    }

    private MessageDto mapToMessageDto(Message message, UUID currentUserId) {
        User sender = message.getSender();
        boolean isSentByMe = sender != null && sender.getId().equals(currentUserId);
        String senderName = sender != null ? sender.getFullName() : "Unknown";
        String senderPhoto = sender != null ? sender.getPhotoUrl() : null;
        String sentAt = message.getSentAt() != null ? message.getSentAt().toString() : null;

        return new MessageDto(
                message.getId(),
                sender != null ? sender.getId() : null,
                senderName,
                senderPhoto,
                message.getContent(),
                sentAt,
                message.isRead(),
                isSentByMe
        );
    }

    private ParticipantDto getOtherParticipant(Conversation conversation, UUID currentUserId) {
        UUID clientUserId = conversation.getClient().getUserId();
        UUID freelancerUserId = conversation.getFreelancer().getUserId();

        UUID otherUserId;
        UUID participantId;

        if (currentUserId.equals(clientUserId)) {
            otherUserId = freelancerUserId;
            participantId = conversation.getFreelancer().getId();
        } else {
            otherUserId = clientUserId;
            participantId = conversation.getClient().getId();
        }

        if (otherUserId == null) {
            return new ParticipantDto(participantId, null, "Unknown", "User", null);
        }

        User otherUser = userRepository.findById(otherUserId).orElse(null);
        if (otherUser == null) {
            return new ParticipantDto(participantId, null, "Unknown", "User", null);
        }

        return new ParticipantDto(
                participantId,
                otherUser.getId(),
                otherUser.getName(),
                otherUser.getSurname(),
                otherUser.getPhotoUrl()
        );
    }
}
