package com.thesis.freelance.features.user.service;

import com.thesis.freelance.features.user.dto.UserNotificationSettingsDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserNotificationSettingsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserNotificationSettingsDto getSettings(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserNotificationSettingsDto updateSettings(UUID userId, UserNotificationSettingsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.emailsOn() != null) user.setEmailsOn(dto.emailsOn());
        if (dto.readOn() != null) user.setReadOn(dto.readOn());
        if (dto.pushOn() != null) user.setPushOn(dto.pushOn());

        userRepository.save(user);
        return toDto(user);
    }

    private UserNotificationSettingsDto toDto(User user) {
        return new UserNotificationSettingsDto(
                user.getEmailsOn(),
                user.getReadOn(),
                user.getPushOn()
        );
    }
}
