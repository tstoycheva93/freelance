package com.thesis.freelance.features.conversation.service;

import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.conversation.dto.*;
import com.thesis.freelance.features.conversation.entity.Conversation;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.message.dto.MessageDto;
import com.thesis.freelance.features.message.entity.Message;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;
    private final OfferRepository offerRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional(readOnly = true)
    public List<ConversationListDto> getConversationsForUser(UUID userId) {
        List<Conversation> conversations = conversationRepository.findAllByUserIdNoFilter(userId);
        
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

        Optional<Client> currentClient = clientRepository.findByUserId(currentUserId);
        Optional<Freelancer> currentFreelancer = freelancerRepository.findByUserId(currentUserId);

        Client client;
        Freelancer freelancer;
        Offer offer = null;

        if (currentClient.isPresent()) {
            client = currentClient.get();
            freelancer = freelancerRepository.findByUserId(request.otherUserId())
                    .orElseThrow(() -> new NoSuchElementException("Freelancer not found"));
        } else if (currentFreelancer.isPresent()) {
            freelancer = currentFreelancer.get();
            client = clientRepository.findByUserId(request.otherUserId())
                    .orElseThrow(() -> new NoSuchElementException("Client not found"));
        } else {
            throw new IllegalStateException("User is neither a client nor a freelancer");
        }

        if (request.offerId() != null) {
            offer = offerRepository.findById(request.offerId())
                    .orElseThrow(() -> new NoSuchElementException("Offer not found"));

            Optional<Conversation> existing = conversationRepository.findExistingConversation(
                    client.getId(), freelancer.getId(), offer.getId());
            if (existing.isPresent()) {
                return getConversationById(existing.get().getId(), currentUserId);
            }
        } else {
            Optional<Conversation> existing = conversationRepository.findExistingConversationWithoutOffer(
                    client.getId(), freelancer.getId());
            if (existing.isPresent()) {
                return getConversationById(existing.get().getId(), currentUserId);
            }
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

        messageRepository.markAsReadByConversationAndReceiver(conversationId, userId);
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
        String sentAt = message.getSentAt() != null ? message.getSentAt().format(TIME_FORMATTER) : null;

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
        boolean isOtherClient;

        if (currentUserId.equals(clientUserId)) {
            otherUserId = freelancerUserId;
            participantId = conversation.getFreelancer().getId();
            isOtherClient = false;
        } else {
            otherUserId = clientUserId;
            participantId = conversation.getClient().getId();
            isOtherClient = true;
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
