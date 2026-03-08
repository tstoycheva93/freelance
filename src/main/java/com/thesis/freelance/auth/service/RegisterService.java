package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.converter.RegisterRequestToUserConverter;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.common.enums.ActivityStatus;
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
    private final VerificationService verificationService;
    private final RegisterRequestToUserConverter converter;
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;

    @Transactional
    public void registerUser(@Valid RegisterRequest request) {
        validateAge(request.getBirthDate());
        checkUsernameAndEmailUniqueness(request.getUsername(), request.getEmail());
        
        User user = converter.convertToUser(request);
        
        // Check if user was previously deleted - reactivate if so
        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (existingUser != null && existingUser.getActivityStatus() == ActivityStatus.DELETED) {
            // Reactivate user with fresh data
            existingUser.setActivityStatus(ActivityStatus.ACTIVE);
            existingUser.setName(user.getName());
            existingUser.setSurname(user.getSurname());
            existingUser.setPassword(user.getPassword());
            existingUser.setBirthDate(user.getBirthDate());
            existingUser.setPhone(user.getPhone());
            // Clear these fields for fresh start
            existingUser.setPhotoUrl("");
            existingUser.setLocation(null);
            existingUser.setIsVerified(false);
            existingUser.setEmailsOn(true);
            existingUser.setReadOn(true);
            existingUser.setPushOn(true);
            userRepository.save(existingUser);
            
            // Create new Freelancer/Client (old one is INACTIVE)
            createProfileBasedOnRole(existingUser);
            return;
        }
        
        userRepository.save(user);
        verificationService.sendVerificationEmail(user.getUsername());
        
        createProfileBasedOnRole(user);
    }
    
    private void checkUsernameAndEmailUniqueness(String username, String email) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new UsernameAlreadyExistsException(username);
        });
        
        // Allow registration if user with email is DELETED
        userRepository.findByEmail(email).ifPresent(u -> {
            if (u.getActivityStatus() != ActivityStatus.DELETED) {
                throw new EmailAlreadyExistsException(email);
            }
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
                .userId(user.getId())
                .build();
        clientRepository.save(client);
    }

    private void createFreelancerProfile(User user) {
        Freelancer freelancer = Freelancer.builder()
                .userId(user.getId())
                .build();
        freelancerRepository.save(freelancer);
    }

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 16) {
            throw new UnderageException(age);
        }
    }
}
