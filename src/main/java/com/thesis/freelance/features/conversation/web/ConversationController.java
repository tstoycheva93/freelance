package com.thesis.freelance.features.conversation.web;

import com.thesis.freelance.features.conversation.dto.ConversationDetailDto;
import com.thesis.freelance.features.conversation.dto.ConversationListDto;
import com.thesis.freelance.features.conversation.dto.CreateConversationRequest;
import com.thesis.freelance.features.conversation.service.ConversationService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ConversationListDto>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        List<ConversationListDto> conversations = conversationService.getConversationsForUser(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDetailDto> getConversation(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        ConversationDetailDto conversation = conversationService.getConversationById(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping
    public ResponseEntity<ConversationDetailDto> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        ConversationDetailDto conversation = conversationService.createConversation(userId, request);
        return ResponseEntity.ok(conversation);
    }

    @PatchMapping("/{conversationId}/messages/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getCurrentUserId(userDetails);
        conversationService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    private UUID getCurrentUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
