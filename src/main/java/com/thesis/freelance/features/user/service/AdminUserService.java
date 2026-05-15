package com.thesis.freelance.features.user.service;

import com.thesis.freelance.auth.service.PasswordResetService;
import com.thesis.freelance.auth.service.VerificationService;
import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.user.dto.AdminUserDetailResponse;
import com.thesis.freelance.features.user.dto.AdminUserDetailResponse.FlagHistoryItem;
import com.thesis.freelance.features.user.dto.AdminUserDto;
import com.thesis.freelance.features.user.dto.AdminUserListResponse;
import com.thesis.freelance.features.user.entity.User;
import com.thesis.freelance.features.user.repository.AdminUserRepository;
import com.thesis.freelance.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepo;
    private final UserRepository userRepository;
    private final UserSecurityService userSecurityService;
    private final PasswordResetService passwordResetService;
    private final VerificationService verificationService;

    public AdminUserListResponse getUsers(int page, int size, String role, String status, String dateFrom) {
        int totalUsers = adminUserRepo.countFiltered(role, status, dateFrom);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalUsers / size));
        if (page > totalPages) page = totalPages;

        List<Object[]> rows = adminUserRepo.findFiltered(role, status, dateFrom, page, size);
        List<AdminUserDto> users = rows.stream().map(this::mapRow).collect(Collectors.toList());

        long totalActiveUsers = adminUserRepo.countTotalActiveUsers();
        long newThisMonth = adminUserRepo.countNewThisMonth();
        long verifiedUsers = adminUserRepo.countVerifiedUsers();

        return new AdminUserListResponse(users, totalUsers, page, totalPages, size, totalActiveUsers, newThisMonth, verifiedUsers);
    }

    public AdminUserDetailResponse getUser(String identifier) {
        Object[] row = adminUserRepo.findByIdentifier(identifier);
        if (row == null) return null;

        AdminUserDto user = mapRow(row);
        UUID userId = toUUID(row[0]);
        String role = (String) row[5];
        double rating = toDouble(row[15]);
        long reviewCount = toLong(row[16]);

        long postedJobs = 0;
        long submitted = 0;
        long completed = 0;

        if ("CLIENT".equalsIgnoreCase(role)) {
            postedJobs = adminUserRepo.countPostingsByClient(userId);
            completed = adminUserRepo.countCompletedOffersForUser(userId);
        } else if ("FREELANCER".equalsIgnoreCase(role)) {
            submitted = adminUserRepo.countOffersSubmittedByFreelancer(userId);
            completed = adminUserRepo.countCompletedOffersForUser(userId);
        }

        var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        List<FlagHistoryItem> recentFlags = new ArrayList<>();
        for (Object[] f : adminUserRepo.findRecentFlags(userId, 5)) {
            String reason = (String) f[0];
            String date = "";
            if (f[1] instanceof LocalDateTime ldt) {
                date = ldt.format(formatter);
            } else if (f[1] instanceof java.sql.Timestamp ts) {
                date = ts.toLocalDateTime().format(formatter);
            } else if (f[1] != null) {
                date = f[1].toString();
            }
            String flagStatus = (String) f[2];
            recentFlags.add(new FlagHistoryItem(reason, date, flagStatus));
        }

        return new AdminUserDetailResponse(user, postedJobs, submitted, completed, rating, reviewCount, recentFlags);
    }

    @Transactional
    public void suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActivityStatus(ActivityStatus.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void reactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActivityStatus(ActivityStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        userSecurityService.deleteAccount(userId);
    }

    public void forcePasswordReset(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        passwordResetService.sendResetEmail(user.getEmail());
    }

    public void forceVerify(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        verificationService.sendVerificationEmail(user.getUsername());
    }

    @Transactional
    public void deleteUserPhoto(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPhotoUrl("");
        userRepository.save(user);
    }

    private AdminUserDto mapRow(Object[] r) {
        UUID id = toUUID(r[0]);
        String name = (String) r[1];
        String surname = (String) r[2];
        String fullName = name + " " + surname;
        String email = (String) r[3];
        String username = (String) r[4];
        String role = (String) r[5];
        String activityStatus = (String) r[6];
        String location = (String) r[9];
        String photoUrl = (String) r[10];
        boolean twoFactor = toBool(r[11]);
        boolean pushOn = toBool(r[12]);
        boolean emailsOn = toBool(r[13]);
        boolean isVerified = toBool(r[14]);

        String registered = "";
        if (r[7] instanceof LocalDateTime createdDt) {
            registered = createdDt.toLocalDate().toString();
        } else if (r[7] instanceof java.sql.Timestamp ts) {
            registered = ts.toLocalDateTime().toLocalDate().toString();
        }

        String born = r[8] != null ? String.valueOf(r[8]) : "";

        String userStatus;
        if ("DELETED".equalsIgnoreCase(activityStatus)) {
            userStatus = "Deleted";
        } else {
            userStatus = "Active".equalsIgnoreCase(activityStatus) ? "Active" : "Inactive";
        }

        return new AdminUserDto(
                id, fullName, email, username, role, userStatus,
                registered, born,
                location != null ? location : "",
                photoUrl != null ? photoUrl : "",
                twoFactor, pushOn, emailsOn, isVerified
        );
    }

    private static UUID toUUID(Object val) {
        if (val instanceof UUID) return (UUID) val;
        if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            long msb = 0, lsb = 0;
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
            return new UUID(msb, lsb);
        }
        return UUID.fromString(val.toString());
    }

    private static long toLong(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private static double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        return "1".equals(val.toString()) || "true".equalsIgnoreCase(val.toString());
    }
}
