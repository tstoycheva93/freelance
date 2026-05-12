package com.thesis.freelance.features.notification.service;

import com.thesis.freelance.auth.service.EmailService;
import com.thesis.freelance.features.notification.dto.NotificationWsDto;
import com.thesis.freelance.common.enums.NotificationType;
import com.thesis.freelance.features.message.service.WebSocketService;
import com.thesis.freelance.features.notification.dto.NotificationDto;
import com.thesis.freelance.features.notification.entity.Notification;
import com.thesis.freelance.features.notification.repository.NotificationRepository;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsForUser(UUID userId, Pageable pageable) {
        var notifications = notificationRepository.findByUserIdOrderByCreatedAtDescNoFilter(userId);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), notifications.size());
        
        var pagedNotifications = notifications.subList(start, end);
        
        var dtos = pagedNotifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, notifications.size());
    }

    @Transactional
    public NotificationDto markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("User not authorized for this notification");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToDto(saved);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        var notifications = notificationRepository.findByUserIdOrderByCreatedAtDescNoFilter(userId);
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    @Transactional
    public NotificationDto createAndNotify(
            UUID recipientId,
            NotificationType type,
            String title,
            String content,
            String pageUrl
    ) {
        User user = userRepository.findById(recipientId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!Boolean.TRUE.equals(user.getPushOn())) {
            log.info("Notification skipped for user {} (push notifications disabled)", recipientId);
            return null;
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .pageUrl(pageUrl)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        if (Boolean.TRUE.equals(user.getEmailsOn())) {
            emailService.sendNotificationEmail(
                    user.getEmail(),
                    title,
                    content,
                    "View Details",
                    pageUrl
            );
        }

        NotificationWsDto wsDto = new NotificationWsDto(
                saved.getId(),
                saved.getType(),
                saved.getTitle(),
                saved.getContent(),
                saved.getPageUrl(),
                saved.isRead(),
                saved.getCreatedAt()
        );
        webSocketService.sendNotificationToUser(recipientId, wsDto);

        return mapToDto(saved);
    }

    public void notifyNewOffer(UUID clientId, UUID offerId, String postingTitle) {
        createAndNotify(clientId, NotificationType.NEW_OFFER,
                "New Offer Received",
                " You've got an offer for " + postingTitle,
                "/offer/" + offerId);
    }

    public void notifyOfferCanceled(UUID clientId, UUID offerId, String postingTitle) {
        createAndNotify(clientId, NotificationType.OFFER_CANCELLED,
                "Offer canceled",
                " An offer for " + postingTitle + " has been canceled by the freelancer.",
                "/offer/" + offerId);
    }

    public void notifyOfferExpiredClient(UUID userId, String postingTitle, UUID offerId) {
        createAndNotify(userId, NotificationType.EXPIRED_OFFER,
                "Offer Expired",
                "Deadline passed for offer to " + postingTitle + ". Wait for deliverables, or cancel and finalize the settlement.",
                "/offer/" + offerId);
    }

    public void notifyOfferExpiredFreelancer(UUID userId, String postingTitle, UUID offerId) {
        createAndNotify(userId, NotificationType.EXPIRED_OFFER,
                "Offer Expired",
                "Deadline passed for offer to " + postingTitle + ". Expect the client to finalize the settlement.",
                "/offer/" + offerId);
    }

    public void notifyOfferExpiredCanceled(UUID userId, String postingTitle, UUID offerId) {
        createAndNotify(userId, NotificationType.EXPIRED_OFFER,
                "Offer Expired",
                "An offer for " + postingTitle + " has expired and has been canceled automatically.",
                "/offer/" + offerId);
    }

    public void notifyPostingExpired(UUID clientId, String postingTitle, UUID postingId) {
        createAndNotify(clientId, NotificationType.EXPIRED_POSTING,
                "Posting Expired",
                "It's past the deadline of " + postingTitle + " and all related offers have been canceled.",
                "/posting/" + postingId);
    }

    public void notifyOfferAccepted(UUID freelancerId, UUID offerId, String postingTitle) {
        createAndNotify(freelancerId, NotificationType.OFFER_ACCEPTED,
                "Offer Accepted!",
                "Your offer for " + postingTitle + " has been accepted. Congrats!",
                "/offer/" + offerId);
    }

    public void notifyOfferRejected(UUID freelancerId, UUID offerId, String postingTitle) {
        createAndNotify(freelancerId, NotificationType.OFFER_REJECTED,
                "Offer Rejected",
                "Your offer for " + postingTitle + " was rejected. You can try again with a better offer.",
                "/offer/" + offerId);
    }

    public void notifyOfferCompleted(UUID freelancerId, UUID offerId, String postingTitle) {
        createAndNotify(freelancerId, NotificationType.OFFER_COMPLETED,
                "Offer Completed",
                "Your offer for " + postingTitle + " was completed by the client. Download your billing receipt.",
                "/offer/" + offerId);
    }

    public void notifyOfferUnsatisfactory(UUID freelancerId, UUID offerId, String postingTitle) {
        createAndNotify(freelancerId, NotificationType.OFFER_UNSATISFACTORY,
                "Offer Unsatisfactory",
                "Your offer for " + postingTitle + " was marked as unsatisfactory and a partial settlement was issued. Download your billing receipt.",
                "/offer/" + offerId);
    }

    public void notifyOfferDelivered(UUID clientId, UUID offerId, String postingTitle) {
        createAndNotify(clientId, NotificationType.OFFER_DELIVERED,
                "Deliverables Available",
                "You received a submission for an offer to " + postingTitle + ". Review and finalize the offer.",
                "/offer/" + offerId);
    }

    public void notifyNewReview(UUID userId) {
        createAndNotify(userId, NotificationType.NEW_REVIEW,
                "New Review Received",
                "You received a new review. Check it out!",
                "/profile/" + userId);
    }

    public void notifyExpiredCertificate(UUID userId, String certificateTitle) {
        createAndNotify(userId, NotificationType.EXPIRED_CERTIFICATE,
                "Expired Certificate",
                "Your certificate '" + certificateTitle + "' has expired and has been hidden from your profile. Renew or remove it now!",
                "/settings/");
    }

    public void notifyRegistrationClosed() {
        List<User> allUsers = userRepository.findNonAdminUsers();
        for (User user : allUsers) {
            createAndNotify(user.getId(), NotificationType.REGISTRATION_CLOSED,
                    "Registration Closed",
                    "Registration has been disabled by the administration.",
                    "/");
        }
    }

    public void notifyNewUser(UUID newUserId, String username) {
        List<User> admins = userRepository.findByUserRole_Role("ADMIN");
        for (User admin : admins) {
            createAndNotify(admin.getId(), NotificationType.NEW_USER,
                    "New User Registered",
                    "User @" + username + " has joined the platform.",
                    "/office/users/" + newUserId);
        }
    }

    public void notifyNewPosting(UUID postingId, String postingTitle) {
        List<User> admins = userRepository.findByUserRole_Role("ADMIN");
        for (User admin : admins) {
            createAndNotify(admin.getId(), NotificationType.NEW_POSTING,
                    "New Posting Created",
                    "A new posting \"" + postingTitle + "\" has been created.",
                    "/office/postings/" + postingId);
        }
    }

    public void notifyNewFlag(UUID flagId) {
        List<User> admins = userRepository.findByUserRole_Role("ADMIN");
        for (User admin : admins) {
            createAndNotify(admin.getId(), NotificationType.NEW_FLAG,
                    "New Flag Reported",
                    "A new flag has been submitted and awaits review.",
                    "/office/flags/" + flagId);
        }
    }

    private NotificationDto mapToDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getContent(),
                notification.getPageUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
