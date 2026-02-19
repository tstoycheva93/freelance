package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.converter.RegisterRequestToUserConverter;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.common.exception.*;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.entity.UserRole;
import com.thesis.freelance.features.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final RegisterRequestToUserConverter converter;
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;

    @Transactional //both operations succeed or fail together
    public void registerUser(@Valid RegisterRequest request) {
        validateAge(request.getBirthDate());
        checkUsernameAndEmailUniqueness(request.getUsername(), request.getEmail());
        
        User user = converter.convertToUser(request);
        User savedUser = userRepository.save(user);
        
        createProfileBasedOnRole(savedUser);
    }
    
    private void checkUsernameAndEmailUniqueness(String username, String email) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new UsernameAlreadyExistsException(username);
        });
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new EmailAlreadyExistsException(email);
        });
    }

    private void createProfileBasedOnRole(User user) {
        UserRole role = user.getUserRole();
        String roleName = role.getRole();
        
        switch (roleName) {
            case "CLIENT":
                createClientProfile(user);
                break;
            case "FREELANCER":
                createFreelancerProfile(user);
                break;
            case "ADMIN": //TODO REMOVE LATER
                break;
            default:
                throw new InvalidRoleException(roleName);
        }
    }

    private void createClientProfile(User user) {
        Client client = Client.builder()
                .userId(user)
                .build();
        clientRepository.save(client);
    }

    private void createFreelancerProfile(User user) {
        Freelancer freelancer = new Freelancer();
        freelancer.setUserId(user.getId());
        freelancer.setAvailable();
        freelancerRepository.save(freelancer);
    }

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 16) {
            throw new UnderageException(age);
        }
    }
}