package com.thesis.freelance.auth.converter;

import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.entity.UserRole;
import com.thesis.freelance.features.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterRequestToUserConverter {

    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;

    public User convertToUser(RegisterRequest request) {

        UserRole role = userRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid roleId: " + request.getRoleId()));

        return User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(role)
                .birthDate(request.getBirthDate())
                .build();
    }
}

