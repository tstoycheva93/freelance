package com.thesis.freelance.features.message.web;

import com.thesis.freelance.features.message.dto.MessageWsDto;
import com.thesis.freelance.features.conversation.dto.SendMessageDto;
import com.thesis.freelance.features.conversation.dto.TypingIndicatorDto;
import com.thesis.freelance.features.conversation.entity.Conversation;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.message.entity.Message;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.message.service.WebSocketService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    @MessageMapping("/chat.sendMessage")
    @SendToUser("/chat/user/recipient")
    @Transactional
    public MessageWsDto handleSendMessage(@Payload SendMessageDto request,
                                          StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new SecurityException("User not authenticated");
        }
        
        String username = principal.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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

        User receiver = isClient 
                ? userRepository.findById(conversation.getFreelancer().getUserId()).orElse(null)
                : userRepository.findById(conversation.getClient().getUserId()).orElse(null);

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

        if (receiver != null) {
            webSocketService.sendMessageToUser(receiver.getId(), dto);
        }

        return dto;
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingIndicatorDto typing,
                                      StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new SecurityException("User not authenticated");
        }
        
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TypingIndicatorDto indicator = new TypingIndicatorDto(
                typing.conversationId(),
                user.getId(),
                user.getFullName(),
                typing.isTyping()
        );

        webSocketService.sendTypingIndicator(typing.conversationId(), indicator);
    }

    @MessageExceptionHandler
    public void handleException(Exception e) {
        log.error("WebSocket message error: {}", e.getMessage());
    }
}
