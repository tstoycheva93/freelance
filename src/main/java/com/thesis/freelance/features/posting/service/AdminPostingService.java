package com.thesis.freelance.features.posting.service;

import com.thesis.freelance.features.posting.dto.*;
import com.thesis.freelance.common.exception.PostingNotFoundException;
import com.thesis.freelance.features.posting.repository.AdminPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminPostingService {

    private final AdminPostingRepository adminPostingRepo;

    public AdminPostingListResponse getPostings(int page, int size, String status, String flagged, String dateFrom, String categoryName) {
        UUID categoryId = null;
        if (categoryName != null && !categoryName.isEmpty() && !categoryName.equalsIgnoreCase("All Categories")) {
            categoryId = adminPostingRepo.findCategoryIdByName(categoryName).orElse(null);
        }

        int totalFiltered = adminPostingRepo.countFiltered(status, flagged, dateFrom, categoryId);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalFiltered / size));
        if (page > totalPages) page = totalPages;

        List<Object[]> rows = adminPostingRepo.findFiltered(status, flagged, dateFrom, categoryId, page, size);
        List<AdminPostingDto> postings = new ArrayList<>();
        for (Object[] r : rows) {
            postings.add(mapRow(r));
        }

        long totalPostings = adminPostingRepo.countTotalPostings();
        long newThisMonth = adminPostingRepo.countNewThisMonth();
        long activePostings = adminPostingRepo.countActivePostings();

        return new AdminPostingListResponse(postings, totalFiltered, page, totalPages, size, totalPostings, newThisMonth, activePostings);
    }

    public AdminPostingDetailDto getPostingDetail(UUID id) {
        Object[] r = adminPostingRepo.findPostingDetailById(id);
        if (r == null) return null;

        UUID rowId = toUUID(r[0]);
        String title = (String) r[1];
        String description = (String) r[2];
        String category = (String) r[3];
        String rawStatus = (String) r[4];
        String status = "ACTIVE".equalsIgnoreCase(rawStatus) ? "Active" : "Closed";
        String rawActivity = (String) r[5];
        String activityStatus = "DELETED".equalsIgnoreCase(rawActivity) ? "Deleted" :
                                "INACTIVE".equalsIgnoreCase(rawActivity) ? "Inactive" : "Active";
        Integer budget = r[6] instanceof Number ? ((Number) r[6]).intValue() : 0;
        Long viewsCount = r[7] instanceof Number ? ((Number) r[7]).longValue() : 0;
        String photoUrl = (String) r[8];
        String datePosted = r[9] != null ? r[9].toString() : "";
        String deadline = "";
        if (r[10] instanceof LocalDate ld) {
            deadline = ld.toString();
        } else if (r[10] instanceof java.sql.Date sd) {
            deadline = sd.toLocalDate().toString();
        } else if (r[10] != null) {
            deadline = r[10].toString();
        }
        String rawExp = (String) r[11];
        String experience = switch (rawExp != null ? rawExp.toUpperCase() : "") {
            case "ENTRY" -> "Entry";
            case "MID" -> "Mid-Level";
            case "SENIOR" -> "Senior";
            case "EXPERT" -> "Expert";
            default -> rawExp != null ? rawExp : "";
        };
        String location = (String) r[12];
        boolean isFlagged = toBool(r[13]);
        String clientName = (String) r[14];
        String clientUsername = (String) r[15];
        UUID clientUserId = toUUID(r[16]);
        double clientRating = r[17] instanceof Number ? ((Number) r[17]).doubleValue() : 0.0;
        long clientReviewCount = r[18] instanceof Number ? ((Number) r[18]).longValue() : 0;

        List<String> skills = splitGroupConcat((String) r[19]);
        List<String> attachmentUrls = splitGroupConcat((String) r[20]);

        List<AdminOfferDto> offers = new ArrayList<>();
        for (Object[] o : adminPostingRepo.findOffersByPostingId(rowId)) {
            UUID offerId = toUUID(o[0]);
            String freelancerName = (String) o[1];
            String freelancerPhotoUrl = (String) o[2];
            BigDecimal price = o[3] instanceof Number ? BigDecimal.valueOf(((Number) o[3]).doubleValue()) : BigDecimal.ZERO;
            String offerStatus = o[4] != null ? o[4].toString() : "";
            String submittedAt = "";
            if (o[5] instanceof LocalDateTime ldt) {
                submittedAt = ldt.toLocalDate().toString();
            } else if (o[5] instanceof java.sql.Timestamp ts) {
                submittedAt = ts.toLocalDateTime().toLocalDate().toString();
            } else if (o[5] != null) {
                submittedAt = o[5].toString();
            }
            String deliveryTime = "";
            if (o[6] instanceof LocalDate ld) {
                deliveryTime = ld.toString();
            } else if (o[6] instanceof java.sql.Date sd) {
                deliveryTime = sd.toLocalDate().toString();
            } else if (o[6] != null) {
                deliveryTime = o[6].toString();
            }
            offers.add(new AdminOfferDto(offerId, freelancerName, freelancerPhotoUrl, price, offerStatus, submittedAt, deliveryTime));
        }

        return new AdminPostingDetailDto(
                rowId, title, description, category, status, activityStatus,
                budget, viewsCount, photoUrl, datePosted, deadline, experience,
                location, skills, attachmentUrls, isFlagged,
                clientName, clientUsername, clientUserId, clientRating, clientReviewCount, offers
        );
    }

    @Transactional
    public void deactivatePosting(UUID id) {
        int updated = adminPostingRepo.deactivatePosting(id);
        if (updated == 0) throw new PostingNotFoundException(id);
    }

    @Transactional
    public void closePosting(UUID id) {
        int updated = adminPostingRepo.closePosting(id);
        if (updated == 0) throw new PostingNotFoundException(id);
    }

    @Transactional
    public void reopenPosting(UUID id) {
        int updated = adminPostingRepo.reopenPosting(id);
        if (updated == 0) throw new PostingNotFoundException(id);
    }

    @Transactional
    public void reactivatePosting(UUID id) {
        int updated = adminPostingRepo.reactivatePosting(id);
        if (updated == 0) throw new PostingNotFoundException(id);
    }

    private List<String> splitGroupConcat(String val) {
        if (val == null || val.isEmpty()) return List.of();
        return Arrays.asList(val.split("\\|\\|"));
    }

    private AdminPostingDto mapRow(Object[] r) {
        UUID id = toUUID(r[0]);
        String title = (String) r[1];
        String category = (String) r[2];
        Integer budget = r[3] instanceof Number ? ((Number) r[3]).intValue() : 0;

        String datePosted = "";
        if (r[4] instanceof LocalDateTime ldt) {
            datePosted = ldt.toLocalDate().toString();
        } else if (r[4] instanceof java.sql.Timestamp ts) {
            datePosted = ts.toLocalDateTime().toLocalDate().toString();
        } else if (r[4] instanceof java.sql.Date d) {
            datePosted = d.toLocalDate().toString();
        } else if (r[4] != null) {
            datePosted = r[4].toString();
        }

        String rawExp = (String) r[5];
        String experience = switch (rawExp != null ? rawExp.toUpperCase() : "") {
            case "ENTRY" -> "Entry";
            case "MID" -> "Mid-Level";
            case "SENIOR" -> "Senior";
            case "EXPERT" -> "Expert";
            default -> rawExp != null ? rawExp : "";
        };

        String location = (String) r[6];
        String rawStatus = (String) r[7];
        String status = "ACTIVE".equalsIgnoreCase(rawStatus) ? "Active" : "Closed";
        boolean isFlagged = toBool(r[8]);
        String rawActivity = (String) r[9];
        String activityStatus = "DELETED".equalsIgnoreCase(rawActivity) ? "Deleted" : "Active";

        return new AdminPostingDto(id, title, category, budget, datePosted, experience, location, status, isFlagged, activityStatus);
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

    private static boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        return "1".equals(val.toString()) || "true".equalsIgnoreCase(val.toString());
    }
}
