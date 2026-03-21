package com.thesis.freelance.features.message.service;

import com.thesis.freelance.common.dto.websocket.MessageWsDto;
import com.thesis.freelance.common.dto.websocket.NotificationWsDto;
import com.thesis.freelance.common.dto.websocket.TypingIndicatorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

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
}
