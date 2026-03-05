package com.thesis.freelance.features.user.service;

import com.thesis.freelance.features.user.dto.UserRoleDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository roleRepository;

    public List<UserRoleDto> getAll() {
        return roleRepository.findAll()
                .stream()
                .filter(role -> !role.getRole().equalsIgnoreCase("ADMIN"))
                .map(role -> new UserRoleDto(
                        role.getId(),
                        toTitleCase(role.getRole())
                ))
                .toList();
    }

    private String toTitleCase(String value) {
        return value.substring(0, 1).toUpperCase() +
                value.substring(1).toLowerCase();
    }
}