package com.thesis.freelance.features.user.service;

import com.thesis.freelance.common.exception.EmailAlreadyExistsException;
import com.thesis.freelance.common.exception.PhoneAlreadyExistsException;
import com.thesis.freelance.features.location.dto.LocationDto;
import com.thesis.freelance.features.location.entity.Location;
import com.thesis.freelance.features.location.repository.LocationRepository;
import com.thesis.freelance.features.user.dto.UserAccountDto;
import com.thesis.freelance.features.user.dto.UserAccountUpdateDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public UserAccountDto getAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserAccountDto updateAccount(UUID userId, UserAccountUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.email() != null && !dto.email().equalsIgnoreCase(user.getEmail())) {
            checkEmailUniqueness(dto.email(), userId);
            user.setEmail(dto.email());
        }

        if (dto.phone() != null && !dto.phone().equals(user.getPhone())) {
            checkPhoneUniqueness(dto.phone(), userId);
            user.setPhone(dto.phone());
        }

        if (dto.name() != null) user.setName(dto.name());
        if (dto.surname() != null) user.setSurname(dto.surname());
        if (dto.photoUrl() != null) {
            user.setPhotoUrl(dto.photoUrl().isBlank() ? null : dto.photoUrl());
        }
        if (dto.isPublic() != null) user.setIsPublic(dto.isPublic());

        if (dto.locationId() != null) {
            Location location = locationRepository.findById(dto.locationId())
                    .orElseThrow(() -> new IllegalArgumentException("Location not found"));
            user.setLocation(location);
        }

        userRepository.save(user);
        return toDto(user);
    }

    private void checkEmailUniqueness(String email, UUID currentUserId) {
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentUserId)) {
                throw new EmailAlreadyExistsException(email);
            }
        });
    }

    private void checkPhoneUniqueness(String phone, UUID currentUserId) {
        userRepository.findByPhone(phone).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentUserId)) {
                throw new PhoneAlreadyExistsException(phone);
            }
        });
    }

    private UserAccountDto toDto(User user) {
        LocationDto locationDto = null;
        if (user.getLocation() != null) {
            locationDto = new LocationDto(user.getLocation().getId(), user.getLocation().getName());
        }

        return new UserAccountDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                locationDto,
                user.getPhotoUrl(),
                user.getBirthDate(),
                user.getIsVerified(),
                user.getIsPublic()
        );
    }
}
