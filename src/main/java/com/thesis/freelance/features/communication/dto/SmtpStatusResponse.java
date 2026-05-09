package com.thesis.freelance.features.communication.dto;

public record SmtpStatusResponse(
        String senderEmail,
        String host,
        int port,
        boolean isConnected,
        String message
) {}
