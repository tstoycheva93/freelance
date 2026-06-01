package com.thesis.freelance.features.message.web;

import com.thesis.freelance.features.conversation.dto.SendMessageDto;
import com.thesis.freelance.features.conversation.dto.TypingIndicatorDto;
import com.thesis.freelance.features.message.dto.MessageWsDto;
import com.thesis.freelance.features.message.service.WebSocketService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final WebSocketService webSocketService;
    private final UserSecurityService userSecurityService;

    @MessageMapping("/chat.sendMessage")
    @SendToUser("/chat/user/recipient")
    public MessageWsDto handleSendMessage(@Payload SendMessageDto request,
                                          StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new SecurityException("User not authenticated");
        }

        User sender = userSecurityService.findByUsername(principal.getName());
        return webSocketService.processAndSendMessage(sender, request);
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingIndicatorDto typing,
                                       StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new SecurityException("User not authenticated");
        }

        User user = userSecurityService.findByUsername(principal.getName());
        webSocketService.processTypingIndicator(user, typing);
    }

    @MessageExceptionHandler
    public void handleException(Exception e) {
        log.error("WebSocket message error: {}", e.getMessage());
    }
}
