package com.thesis.freelance.features.flag.service;

import com.thesis.freelance.common.enums.FlagStatus;
import com.thesis.freelance.common.enums.FlagTarget;
import com.thesis.freelance.common.enums.FlagType;
import com.thesis.freelance.features.flag.entity.Flag;
import com.thesis.freelance.features.flag.repository.FlagRepository;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlagService {

    private final FlagRepository flagRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Flag createFlag(FlagTarget target, UUID targetId, FlagType type, String reason, UUID reporterUserId) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found"));

        if (flagRepository.existsByTargetAndTargetIdAndReporter(target, targetId, reporter)) {
            return null;
        }

        Flag flag = Flag.builder()
                .target(target)
                .targetId(targetId)
                .type(type != null ? type : FlagType.OTHER)
                .reason(reason)
                .status(FlagStatus.PENDING)
                .reporter(reporter)
                .build();

        Flag saved = flagRepository.save(flag);
        notificationService.notifyNewFlag(saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Flag> getPendingFlags(Pageable pageable) {
        return flagRepository.findByStatus(FlagStatus.PENDING, pageable);
    }

    @Transactional
    public Flag reviewFlag(UUID flagId, FlagStatus status, String reason, UUID adminUserId) {
        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("Flag not found"));

        if (flag.getStatus() != FlagStatus.PENDING) {
            throw new IllegalStateException("Flag has already been reviewed");
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        flag.setStatus(status);
        flag.setReviewer(admin);
        flag.setReviewTime(LocalDate.now());
        flag.setReason(reason);

        return flagRepository.save(flag);
    }

    @Transactional(readOnly = true)
    public Flag getFlagById(UUID flagId) {
        return flagRepository.findById(flagId)
                .orElseThrow(() -> new IllegalArgumentException("Flag not found"));
    }
}