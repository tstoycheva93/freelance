package com.thesis.freelance.auth.service;

import com.thesis.freelance.features.user.dto.UserDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMeService {

    private final UserRepository userRepository;

    public UserDto getMe(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getRoleName(),
                user.getPhone(),
                user.getBirthDate(),
                null,                        // you don’t have location field currently
                user.getPhotoUrl()
        );
    }
}