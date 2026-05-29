package com.thesis.freelance.auth.service;

import com.thesis.freelance.auth.converter.RegisterRequestToUserConverter;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.exception.*;
import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.system.entity.SystemConfig;
import com.thesis.freelance.features.system.service.SystemConfigService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.entity.UserRole;
import com.thesis.freelance.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationService verificationService;

    @Mock
    private RegisterRequestToUserConverter converter;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RegisterService registerService;

    private RegisterRequest request;
    private User user;
    private UserRole clientRole;
    private UserRole freelancerRole;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setName("John");
        request.setSurname("Doe");
        request.setPhone("+1234567890");
        request.setPassword("Password123!");
        request.setBirthDate(LocalDate.now().minusYears(20));
        request.setRoleId(UUID.randomUUID());

        clientRole = UserRole.builder().role("CLIENT").build();
        freelancerRole = UserRole.builder().role("FREELANCER").build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .name("John")
                .surname("Doe")
                .phone("+1234567890")
                .password("Password123!")
                .birthDate(LocalDate.now().minusYears(20))
                .userRole(clientRole)
                .build();
    }

    @Test
    void testRegisterUser_SuccessfulClient() {
        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(converter.convertToUser(request)).thenReturn(user);

        assertDoesNotThrow(() -> registerService.registerUser(request));

        verify(userRepository).save(user);
        verify(verificationService).sendVerificationEmail("testuser");
        verify(clientRepository).save(any(Client.class));
        verify(notificationService).notifyNewUser(user.getId(), "testuser");
        verifyNoInteractions(freelancerRepository);
    }

    @Test
    void testRegisterUser_SuccessfulFreelancer() {
        user.setUserRole(freelancerRole);
        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(converter.convertToUser(request)).thenReturn(user);

        assertDoesNotThrow(() -> registerService.registerUser(request));

        verify(userRepository).save(user);
        verify(verificationService).sendVerificationEmail("testuser");
        verify(freelancerRepository).save(any(Freelancer.class));
        verify(notificationService).notifyNewUser(user.getId(), "testuser");
        verifyNoInteractions(clientRepository);
    }

    @Test
    void testRegisterUser_RegistrationDisabled() {
        when(systemConfigService.isRegistrationEnabled()).thenReturn(false);
        SystemConfig config = new SystemConfig();
        config.setRegistrationMessage("Registration is currently disabled.");
        when(systemConfigService.getConfig()).thenReturn(config);

        assertThrows(RegistrationDisabledException.class, () -> registerService.registerUser(request));

        verifyNoInteractions(userRepository, verificationService, converter,
                clientRepository, freelancerRepository, notificationService);
    }

    @Test
    void testRegisterUser_Underage() {
        request.setBirthDate(LocalDate.now().minusYears(15));
        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);

        assertThrows(UnderageException.class, () -> registerService.registerUser(request));

        verifyNoInteractions(userRepository, verificationService, converter,
                clientRepository, freelancerRepository, notificationService);
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> registerService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> registerService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_ReactivateDeletedUser() {
        User deletedUser = User.builder()
                .username("olduser")
                .email("test@example.com")
                .userRole(clientRole)
                .build();
        deletedUser.setActivityStatus(ActivityStatus.DELETED);

        when(systemConfigService.isRegistrationEnabled()).thenReturn(true);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(deletedUser));
        when(converter.convertToUser(request)).thenReturn(user);

        assertDoesNotThrow(() -> registerService.registerUser(request));

        assertEquals(ActivityStatus.ACTIVE, deletedUser.getActivityStatus());
        assertEquals("testuser", deletedUser.getUsername());
        verify(userRepository).save(deletedUser);
        verify(clientRepository).save(any(Client.class));
    }
}
