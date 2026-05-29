package com.thesis.freelance.auth.web;

import com.thesis.freelance.auth.dto.LoginRequest;
import com.thesis.freelance.auth.dto.LoginResponse;
import com.thesis.freelance.auth.dto.RegisterRequest;
import com.thesis.freelance.auth.service.*;
import com.thesis.freelance.common.exception.GlobalExceptionHandler;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.service.AdminInviteService;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegisterService registerService;

    @Mock
    private LoginService loginService;

    @Mock
    private VerificationService verificationService;

    @Mock
    private AuthMeService authMeService;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private AdminInviteService adminInviteService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testRegister_Success() throws Exception {
        String requestJson = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "name": "John",
                    "surname": "Doe",
                    "phone": "+359888123456",
                    "password": "Password123!",
                    "birthDate": "20-05-2000",
                    "roleId": "%s"
                }
                """.formatted(UUID.randomUUID().toString());

        doNothing().when(registerService).registerUser(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(registerService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void testRegister_ValidationError() throws Exception {
        String requestJson = """
                {
                    "username": "",
                    "email": "bad-email",
                    "name": "John",
                    "surname": "Doe",
                    "phone": "invalid",
                    "password": "short",
                    "birthDate": "20-05-2000",
                    "roleId": "%s"
                }
                """.formatted(UUID.randomUUID().toString());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerService);
    }

    @Test
    void testLogin_Success() throws Exception {
        String token = "jwt-token-123";
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .requires2fa(false)
                .message("Login successful")
                .build();

        when(loginService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"Password123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.requires2fa").value(false))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(loginService).login(any(LoginRequest.class));
    }
}
