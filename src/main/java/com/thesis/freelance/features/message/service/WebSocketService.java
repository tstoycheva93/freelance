package com.thesis.freelance.features.message.service;

import com.thesis.freelance.features.conversation.dto.SendMessageDto;
import com.thesis.freelance.features.conversation.dto.TypingIndicatorDto;
import com.thesis.freelance.features.conversation.entity.Conversation;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.message.dto.MessageWsDto;
import com.thesis.freelance.features.message.dto.ReadStatusUpdateDto;
import com.thesis.freelance.features.message.entity.Message;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.notification.dto.NotificationWsDto;
import com.thesis.freelance.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public void sendMessageToUser(UUID userId, MessageWsDto message) {
        messagingTemplate.convertAndSend("/chat/user/" + userId, message);
        log.debug("Message sent to /chat/user/{}: {}", userId, message.id());
    }

    public void sendNotificationToUser(UUID userId, NotificationWsDto notification) {
        messagingTemplate.convertAndSend("/notifications/user/" + userId, notification);
        log.debug("Notification sent to /notifications/user/{}: {}", userId, notification.id());
    }

    public void sendTypingIndicator(UUID conversationId, TypingIndicatorDto typing) {
        messagingTemplate.convertAndSend("/chat/conversation/" + conversationId + "/typing", typing);
        log.debug("Typing indicator sent to /chat/conversation/{}/typing", conversationId);
    }

    public void sendMessageReadStatus(UUID userId, ReadStatusUpdateDto readStatusUpdate) {
        messagingTemplate.convertAndSend("/chat/user/" + userId, readStatusUpdate);
        log.debug("Read status update sent to /chat/user/{}", userId);
    }

    @Transactional
    public MessageWsDto processAndSendMessage(User sender, SendMessageDto request) {
        if (request.clientMessageId() != null) {
            boolean exists = messageRepository.existsByClientMessageId(request.clientMessageId());
            if (exists) {
                log.debug("Duplicate message ignored: {}", request.clientMessageId());
                return null;
            }
        }

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        boolean isClient = conversation.getClient().getUserId().equals(sender.getId());
        boolean isFreelancer = conversation.getFreelancer().getUserId() != null
                && conversation.getFreelancer().getUserId().equals(sender.getId());

        if (!isClient && !isFreelancer) {
            throw new SecurityException("Not authorized to send message in this conversation");
        }

        UUID receiverId = isClient
                ? conversation.getFreelancer().getUserId()
                : conversation.getClient().getUserId();

        User receiver = new User();
        receiver.setId(receiverId);

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .clientMessageId(request.clientMessageId())
                .build();

        Message savedMessage = messageRepository.save(message);

        conversation.updateLastMsgAt(savedMessage.getSentAt());
        conversationRepository.save(conversation);

        MessageWsDto dto = new MessageWsDto(
                savedMessage.getId(),
                conversation.getId(),
                sender.getId(),
                sender.getFullName(),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                request.clientMessageId()
        );

        sendMessageToUser(receiverId, dto);

        return dto;
    }

    public void processTypingIndicator(User user, TypingIndicatorDto typing) {
        TypingIndicatorDto indicator = new TypingIndicatorDto(
                typing.conversationId(),
                user.getId(),
                user.getFullName(),
                typing.isTyping()
        );

        sendTypingIndicator(typing.conversationId(), indicator);
    }
}
