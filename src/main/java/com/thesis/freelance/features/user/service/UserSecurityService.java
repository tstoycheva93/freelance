package com.thesis.freelance.features.user.service;

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
import com.thesis.freelance.features.transaction.repository.TransactionRepository;
import com.thesis.freelance.features.user.dto.ChangePasswordDto;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import com.thesis.freelance.features.uploadedCV.repository.UploadedCVRepository;
import com.thesis.freelance.features.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    private final TransactionRepository transactionRepository;
    private final ReviewRepository reviewRepository;

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

        // Delete Freelancer/Client (hard delete)
        freelancerRepository.deleteByUserId(userId);
        clientRepository.deleteByUserId(userId);

        // Deactivate all linked data (INACTIVE)
        deactivateFreelancerLinkedData(userId);
        deactivateClientLinkedData(userId);
        deactivateUserLinkedData(userId);

        // Set user to DELETED
        user.setActivityStatus(ActivityStatus.DELETED);
        userRepository.save(user);
    }

    private void deactivateFreelancerLinkedData(UUID userId) {
        workRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        educationRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        certificationRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        uploadedCVRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        freelancerSkillsRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        offerRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        savedPostingRepository.findAllByFreelancerUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        workRepository.saveAll(workRepository.findAllByFreelancerUserId(userId));
        educationRepository.saveAll(educationRepository.findAllByFreelancerUserId(userId));
        certificationRepository.saveAll(certificationRepository.findAllByFreelancerUserId(userId));
        uploadedCVRepository.saveAll(uploadedCVRepository.findAllByFreelancerUserId(userId));
        freelancerSkillsRepository.saveAll(freelancerSkillsRepository.findAllByFreelancerUserId(userId));
        offerRepository.saveAll(offerRepository.findAllByFreelancerUserId(userId));
        savedPostingRepository.saveAll(savedPostingRepository.findAllByFreelancerUserId(userId));
    }

    private void deactivateClientLinkedData(UUID userId) {
        postingRepository.findAllByClientUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        shortlistedOfferRepository.findAllByClientUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        postingRepository.saveAll(postingRepository.findAllByClientUserId(userId));
        shortlistedOfferRepository.saveAll(shortlistedOfferRepository.findAllByClientUserId(userId));
    }

    private void deactivateUserLinkedData(UUID userId) {
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        conversationRepository.findAllByUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        messageRepository.findAllByUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        transactionRepository.findAllByUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));
        reviewRepository.findAllByUserId(userId).forEach(e -> e.setActivityStatus(ActivityStatus.INACTIVE));

        notificationRepository.saveAll(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
        conversationRepository.saveAll(conversationRepository.findAllByUserId(userId));
        messageRepository.saveAll(messageRepository.findAllByUserId(userId));
        transactionRepository.saveAll(transactionRepository.findAllByUserId(userId));
        reviewRepository.saveAll(reviewRepository.findAllByUserId(userId));
    }
}
