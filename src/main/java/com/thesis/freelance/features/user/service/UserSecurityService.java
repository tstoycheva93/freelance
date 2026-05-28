package com.thesis.freelance.features.user.service;

import com.thesis.freelance.auth.entity.TrustedDevice;
import com.thesis.freelance.auth.repository.TrustedDeviceRepository;
import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.certification.repository.CertificationRepository;
import com.thesis.freelance.features.client.repository.ClientRepository;
import com.thesis.freelance.features.conversation.repository.ConversationRepository;
import com.thesis.freelance.features.education.repository.EducationRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerRepository;
import com.thesis.freelance.features.freelancer.repository.FreelancerSkillsRepository;
import com.thesis.freelance.features.message.repository.MessageRepository;
import com.thesis.freelance.features.notification.repository.NotificationRepository;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.offer.repository.ShortlistedOfferRepository;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import com.thesis.freelance.features.posting.repository.SavedPostingRepository;
import com.thesis.freelance.features.review.repository.ReviewRepository;
import com.thesis.freelance.features.user.dto.ChangePasswordDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.uploadedCV.repository.UploadedCVRepository;
import com.thesis.freelance.features.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;
    private final WorkRepository workRepository;
    private final EducationRepository educationRepository;
    private final CertificationRepository certificationRepository;
    private final UploadedCVRepository uploadedCVRepository;
    private final FreelancerSkillsRepository freelancerSkillsRepository;
    private final PostingRepository postingRepository;
    private final OfferRepository offerRepository;
    private final ShortlistedOfferRepository shortlistedOfferRepository;
    private final SavedPostingRepository savedPostingRepository;
    private final NotificationRepository notificationRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final TrustedDeviceRepository trustedDeviceRepository;

    @Transactional
    public void changePassword(UUID userId, ChangePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password");
        }

        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Deactivate Freelancer/Client (soft delete)
        freelancerRepository.findByUserId(userId).ifPresent(freelancer -> {
            freelancer.setActivityStatus(ActivityStatus.DELETED);
            freelancerRepository.save(freelancer);
        });
        clientRepository.findByUserId(userId).ifPresent(client -> {
            client.setActivityStatus(ActivityStatus.DELETED);
            clientRepository.save(client);
        });

        // Deactivate all linked data (INACTIVE)
        deactivateFreelancerLinkedData(userId);
        deactivateClientLinkedData(userId);
        deactivateUserLinkedData(userId);

        // Set user to DELETED
        user.setActivityStatus(ActivityStatus.DELETED);
        userRepository.save(user);
    }

    public boolean get2FAStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getIs2faEnabled() != null && user.getIs2faEnabled();
    }

    @Transactional
    public void set2FAStatus(UUID userId, Boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setIs2faEnabled(enabled);
        userRepository.save(user);
    }

    public List<?> getTrustedDevices(UUID userId) {
        return trustedDeviceRepository.findByUserId(userId).stream()
                .map(device -> java.util.Map.of(
                        "id", device.getId().toString(),
                        "deviceId", device.getDeviceId(),
                        "deviceName", device.getDeviceName() != null ? device.getDeviceName() : "Unknown Device",
                        "createdAt", device.getCreatedAt() != null ? device.getCreatedAt().toString() : ""
                ))
                .collect(Collectors.toList());
    }

    private void deactivateFreelancerLinkedData(UUID userId) {
        workRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        educationRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        certificationRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        uploadedCVRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        freelancerSkillsRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        offerRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        savedPostingRepository.findAllByFreelancerUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        workRepository.saveAll(workRepository.findAllByFreelancerUserIdNoFilter(userId));
        educationRepository.saveAll(educationRepository.findAllByFreelancerUserIdNoFilter(userId));
        certificationRepository.saveAll(certificationRepository.findAllByFreelancerUserIdNoFilter(userId));
        uploadedCVRepository.saveAll(uploadedCVRepository.findAllByFreelancerUserIdNoFilter(userId));
        freelancerSkillsRepository.saveAll(freelancerSkillsRepository.findAllByFreelancerUserIdNoFilter(userId));
        offerRepository.saveAll(offerRepository.findAllByFreelancerUserIdNoFilter(userId));
        savedPostingRepository.saveAll(savedPostingRepository.findAllByFreelancerUserIdNoFilter(userId));
    }

    private void deactivateClientLinkedData(UUID userId) {
        postingRepository.findAllByClientUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        shortlistedOfferRepository.findAllByClientUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        postingRepository.saveAll(postingRepository.findAllByClientUserIdNoFilter(userId));
        shortlistedOfferRepository.saveAll(shortlistedOfferRepository.findAllByClientUserIdNoFilter(userId));
    }

    private void deactivateUserLinkedData(UUID userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDescNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        conversationRepository.findAllByUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        messageRepository.findAllByUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        reviewRepository.findAllByUserIdNoFilter(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        notificationRepository.saveAll(notificationRepository.findByUserIdOrderByCreatedAtDescNoFilter(userId));
        conversationRepository.saveAll(conversationRepository.findAllByUserIdNoFilter(userId));
        messageRepository.saveAll(messageRepository.findAllByUserIdNoFilter(userId));
        reviewRepository.saveAll(reviewRepository.findAllByUserIdNoFilter(userId));
    }
}
