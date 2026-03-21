package com.thesis.freelance.features.message.dto;

import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID senderId,
    String senderName,
    String senderPhoto,
    String content,
    String sentAt,
    boolean isRead,
    boolean isSentByMe
) {}
