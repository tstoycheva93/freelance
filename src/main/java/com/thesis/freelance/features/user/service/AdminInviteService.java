package com.thesis.freelance.features.user.service;

import com.thesis.freelance.auth.service.EmailService;
import com.thesis.freelance.common.exception.EmailAlreadyExistsException;
import com.thesis.freelance.common.exception.UsernameAlreadyExistsException;
import com.thesis.freelance.features.user.dto.*;
import com.thesis.freelance.features.user.entity.AdminInvite;
import com.thesis.freelance.features.user.entity.InviteStatus;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.entity.UserRole;
import com.thesis.freelance.features.user.repository.AdminInviteRepository;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.user.repository.UserRoleRepository;
import com.thesis.freelance.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInviteService {

    private final AdminInviteRepository adminInviteRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url-front}")
    private String baseUrl;

    private static final long INVITE_TOKEN_EXPIRY_HOURS = 24;

    @Transactional
    public void sendInvite(AdminInviteRequest request, UUID requestingAdminId) {
        User admin = userRepository.findById(requestingAdminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        adminInviteRepository.findByEmail(request.email()).ifPresent(existing -> {
            if (existing.getStatus() == InviteStatus.PENDING) {
                if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
                    existing.setStatus(InviteStatus.EXPIRED);
                    adminInviteRepository.save(existing);
                } else {
                    throw new IllegalArgumentException("An active invite already exists for this email");
                }
            }
        });

        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("A user with this email is already registered on the platform");
        });

        AdminInvite invite = AdminInvite.builder()
                .email(request.email())
                .status(InviteStatus.PENDING)
                .invitedBy(admin)
                .expiresAt(LocalDateTime.now().plusHours(INVITE_TOKEN_EXPIRY_HOURS))
                .build();
        adminInviteRepository.save(invite);

        String token = jwtTokenProvider.generateVerificationToken(
                invite.getId().toString(),
                INVITE_TOKEN_EXPIRY_HOURS * 60 * 60 * 1000
        );
        String inviteLink = baseUrl + "/accept-invite?token=" + token;

        emailService.sendAdminInviteEmail(request.email(), inviteLink);
        log.info("Admin invite sent to: {} by admin: {}", request.email(), requestingAdminId);
    }

    public UUID validateInviteToken(String token) {
        try {
            Claims claims = jwtTokenProvider.validateAndGetClaims(token);
            return UUID.fromString(claims.getSubject());
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid or expired invite token");
        }
    }

    public AdminInvite getValidInvite(String token) {
        UUID inviteId = validateInviteToken(token);

        AdminInvite invite = adminInviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Invite not found"));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalArgumentException("Invite is no longer pending");
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            adminInviteRepository.save(invite);
            throw new IllegalArgumentException("Invite has expired");
        }

        return invite;
    }

    public String validateAndGetEmail(String token) {
        return getValidInvite(token).getEmail();
    }

    @Transactional
    public void registerAdmin(AdminRegistrationRequest request) {
        AdminInvite invite = getValidInvite(request.token());

        userRepository.findByUsername(request.username()).ifPresent(u -> {
            throw new UsernameAlreadyExistsException(request.username());
        });

        userRepository.findByEmail(invite.getEmail()).ifPresent(u -> {
            throw new EmailAlreadyExistsException(invite.getEmail());
        });

        UserRole adminRole = userRoleRepository.findByRole("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User user = User.builder()
                .userRole(adminRole)
                .name(request.name())
                .surname(request.surname())
                .username(request.username())
                .email(invite.getEmail())
                .password(passwordEncoder.encode(request.password()))
                .birthDate(request.birthDate())
                .isVerified(true)
                .isPublic(false)
                .location(null)
                .build();
        userRepository.save(user);

        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setAcceptedAt(LocalDateTime.now());
        adminInviteRepository.save(invite);

        log.info("Admin registered via invite: {} (email: {})", request.username(), invite.getEmail());
    }

    @Transactional(readOnly = true)
    public List<AdminInviteDto> getInvites() {
        List<AdminInvite> invites = adminInviteRepository.findAllByOrderByCreatedAtDesc();

        LocalDateTime now = LocalDateTime.now();
        for (AdminInvite invite : invites) {
            if (invite.getStatus() == InviteStatus.PENDING && invite.getExpiresAt().isBefore(now)) {
                invite.setStatus(InviteStatus.EXPIRED);
            }
        }
        adminInviteRepository.saveAll(invites);

        return invites.stream()
                .filter(inv -> inv.getStatus() != InviteStatus.ACCEPTED)
                .map(this::toInviteDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminDto> getActiveAdmins() {
        List<User> admins = userRepository.findByUserRole_Role("ADMIN");
        return admins.stream()
                .map(u -> new AdminDto(
                        u.getId(),
                        u.getName(),
                        u.getSurname(),
                        u.getEmail(),
                        u.getIsVerified()
                ))
                .toList();
    }

    private AdminInviteDto toInviteDto(AdminInvite invite) {
        return new AdminInviteDto(
                invite.getId(),
                invite.getEmail(),
                invite.getStatus(),
                invite.getInvitedBy().getFullName(),
                invite.getCreatedAt(),
                invite.getExpiresAt(),
                invite.getAcceptedAt()
        );
    }
}
