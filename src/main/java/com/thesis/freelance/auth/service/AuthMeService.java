package com.thesis.freelance.auth.service;

import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.location.dto.LocationDto;
import com.thesis.freelance.features.user.dto.UserDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthMeService {

    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;

    public UserDto getMe(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID freelancerId = freelancerRepository.findByUserId(user.getId())
                .map(f -> f.getId())
                .orElse(null);

        UUID clientId = clientRepository.findByUserId(user.getId())
                .map(f -> f.getId())
                .orElse(null);

        LocationDto locationDto = null;
        if (user.getLocation() != null) {
            locationDto = new LocationDto(user.getLocation().getId(), user.getLocation().getName(), user.getLocation().getLatitude(), user.getLocation().getLongitude());
        }

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getRoleName(),
                user.getPhone(),
                user.getBirthDate(),
                locationDto,
                user.getPhotoUrl(),
                freelancerId,
                clientId,
                user.getIsVerified()
        );
    }
}
