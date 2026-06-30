package com.thesis.freelance.features.flag.service;

import com.thesis.freelance.features.flag.dto.AdminFlagDetailDto;
import com.thesis.freelance.features.flag.dto.AdminFlagDto;
import com.thesis.freelance.features.flag.dto.AdminFlagListResponse;
import com.thesis.freelance.features.flag.dto.AdminFlagReviewRequest;
import com.thesis.freelance.common.exception.FlagAlreadyReviewedException;
import com.thesis.freelance.common.exception.FlagNotFoundException;
import com.thesis.freelance.features.flag.repository.AdminFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFlagService {

    private final AdminFlagRepository adminFlagRepo;

    public AdminFlagListResponse getFlags(int page, int size, String status, String target) {
        int totalFiltered = adminFlagRepo.countFiltered(status, target);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalFiltered / size));
        if (page > totalPages) page = totalPages;

        List<Object[]> rows = adminFlagRepo.findFiltered(status, target, page, size);
        List<AdminFlagDto> flags = new ArrayList<>();
        for (Object[] r : rows) {
            flags.add(mapRow(r));
        }

        long totalFlags = adminFlagRepo.countTotal();
        long pendingCount = adminFlagRepo.countByStatus("PENDING");
        long approvedCount = adminFlagRepo.countByStatus("APPROVED");
        long declinedCount = adminFlagRepo.countByStatus("DECLINED");

        return new AdminFlagListResponse(flags, totalFiltered, page, totalPages, size,
                totalFlags, pendingCount, approvedCount, declinedCount);
    }

    private AdminFlagDto mapRow(Object[] r) {
        UUID id = toUUID(r[0]);
        String rawTarget = (String) r[1];
        String targetType = formatTarget(rawTarget);
        String targetDisplay = r[2] != null ? r[2].toString() : "";
        String rawType = (String) r[3];
        String flagType = formatFlagType(rawType);
        String reporterName = r[4] != null ? r[4].toString() : "";
        String createdAt = r[5] != null ? r[5].toString() : "";
        String rawStatus = (String) r[6];
        String status = formatStatus(rawStatus);

        return new AdminFlagDto(id, targetType, targetDisplay, flagType, reporterName, createdAt, status);
    }

    private String formatTarget(String raw) {
        if (raw == null) return "";
        return switch (raw.toUpperCase()) {
            case "FLAG_PROFILE" -> "Profile";
            case "FLAG_POSTING" -> "Posting";
            case "FLAG_REVIEW" -> "Review";
            default -> raw;
        };
    }

    private String formatFlagType(String raw) {
        if (raw == null) return "";
        return switch (raw.toUpperCase()) {
            case "SPAM" -> "Spam";
            case "INAPPROPRIATE" -> "Inappropriate";
            case "OTHER" -> "Other";
            case "ILLEGAL_ACTIVITY" -> "Illegal Activity";
            case "ABUSE" -> "Abuse";
            case "FRAUD" -> "Fraud";
            default -> raw;
        };
    }

    private String formatStatus(String raw) {
        if (raw == null) return "PENDING";
        return raw.toUpperCase();
    }

    public AdminFlagDetailDto getFlagDetail(UUID id) {
        Object[] r = adminFlagRepo.findFlagDetailById(id);
        if (r == null) throw new FlagNotFoundException(id);

        UUID flagId = toUUID(r[0]);
        String targetType = formatTarget((String) r[1]);
        UUID targetId = toUUID(r[2]);
        String targetDisplay = r[3] != null ? r[3].toString() : "";
        String flagType = formatFlagType((String) r[4]);
        String status = formatStatus((String) r[5]);
        String reporterName = r[6] != null ? r[6].toString() : "";
        UUID reporterUserId = r[7] != null ? toUUID(r[7]) : null;
        String createdAt = r[8] != null ? r[8].toString() : "";
        String reviewerName = r[9] != null ? r[9].toString() : null;
        String reviewReason = r[10] != null ? r[10].toString() : null;
        String reviewTime = r[11] != null ? r[11].toString() : null;
        String reviewContent = r[12] != null ? r[12].toString() : null;

        return new AdminFlagDetailDto(flagId, targetType, targetId, targetDisplay,
                flagType, status, reporterName, reporterUserId, createdAt,
                reviewerName, reviewReason, reviewTime, reviewContent);
    }

    @Transactional
    public void reviewFlag(UUID id, AdminFlagReviewRequest request, UUID adminUserId) {
        int updated = adminFlagRepo.reviewFlag(id, request.status().name(), request.reason(), adminUserId);
        if (updated == 0) {
            AdminFlagDetailDto current = getFlagDetail(id);
            if (current == null) throw new FlagNotFoundException(id);
            if (!"PENDING".equals(current.status())) {
                throw new FlagAlreadyReviewedException(id);
            }
            throw new FlagNotFoundException(id);
        }
    }

    private static UUID toUUID(Object val) {
        if (val instanceof UUID u) return u;
        if (val instanceof byte[] bytes) {
            long msb = 0, lsb = 0;
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
            return new UUID(msb, lsb);
        }
        return UUID.fromString(val.toString());
    }
}
