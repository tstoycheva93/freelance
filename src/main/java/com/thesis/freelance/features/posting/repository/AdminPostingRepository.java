package com.thesis.freelance.features.posting.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public class AdminPostingRepository {

    @PersistenceContext
    private EntityManager em;

    public int countFiltered(String status, String flagged, String dateFrom, UUID categoryId) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM posting p
            LEFT JOIN category c ON p.category_id = c.id
            WHERE 1=1
            """);
        List<String> clauses = new ArrayList<>();
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            clauses.add("p.status = :status");
        }
        if (flagged != null && !flagged.isEmpty() && !flagged.equalsIgnoreCase("No Preference")) {
            if (flagged.equalsIgnoreCase("Yes, Flagged")) {
                clauses.add("EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING')");
            } else {
                clauses.add("NOT EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING')");
            }
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            clauses.add("p.created_at >= :dateFrom");
        }
        if (categoryId != null) {
            clauses.add("p.category_id = :categoryId");
        }
        for (String c : clauses) {
            sql.append(" AND ").append(c);
        }

        var query = em.createNativeQuery(sql.toString());
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            query.setParameter("status", status.toUpperCase());
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            query.setParameter("dateFrom", java.sql.Date.valueOf(java.time.LocalDate.parse(dateFrom)));
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        return ((Number) query.getSingleResult()).intValue();
    }

    public List<Object[]> findFiltered(String status, String flagged, String dateFrom, UUID categoryId, int page, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.title, COALESCE(c.name, ''), p.budget,
                   DATE_FORMAT(p.created_at, '%Y-%m-%d'),
                   p.experience_level, COALESCE(l.name, 'Remotely'),
                   p.status,
                   EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING'),
                   p.activity_status
            FROM posting p
            LEFT JOIN category c ON p.category_id = c.id
            LEFT JOIN location l ON p.location_id = l.id
            WHERE 1=1
            """);

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            sql.append(" AND p.status = :status");
        }
        if (flagged != null && !flagged.isEmpty() && !flagged.equalsIgnoreCase("No Preference")) {
            if (flagged.equalsIgnoreCase("Yes, Flagged")) {
                sql.append(" AND EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING')");
            } else {
                sql.append(" AND NOT EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING')");
            }
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append(" AND p.created_at >= :dateFrom");
        }
        if (categoryId != null) {
            sql.append(" AND p.category_id = :categoryId");
        }
        sql.append(" ORDER BY p.created_at DESC LIMIT :limit OFFSET :offset");

        var query = em.createNativeQuery(sql.toString());
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            query.setParameter("status", status.toUpperCase());
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            query.setParameter("dateFrom", java.sql.Date.valueOf(java.time.LocalDate.parse(dateFrom)));
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        query.setParameter("limit", size);
        query.setParameter("offset", (page - 1) * size);

        return query.getResultList();
    }

    public long countTotalPostings() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM posting WHERE activity_status = 'ACTIVE'")
                .getSingleResult()).longValue();
    }

    public long countNewThisMonth() {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM posting
            WHERE activity_status = 'ACTIVE'
              AND created_at >= DATE_FORMAT(NOW(), '%Y-%m-01')
            """).getSingleResult()).longValue();
    }

    public long countActivePostings() {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM posting
            WHERE activity_status = 'ACTIVE' AND status = 'ACTIVE'
            """).getSingleResult()).longValue();
    }

    public Optional<UUID> findCategoryIdByName(String name) {
        var result = em.createNativeQuery("SELECT id FROM category WHERE name = :name AND activity_status = 'ACTIVE'")
                .setParameter("name", name).getResultList();
        if (result.isEmpty()) return Optional.empty();
        Object val = result.get(0);
        if (val instanceof UUID u) return Optional.of(u);
        if (val instanceof byte[] bytes) {
            long msb = 0, lsb = 0;
            for (int i = 0; i < 8; i++) msb = (msb << 8) | (bytes[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (bytes[i] & 0xff);
            return Optional.of(new UUID(msb, lsb));
        }
        return Optional.of(UUID.fromString(val.toString()));
    }

    public Object[] findPostingDetailById(UUID id) {
        var query = em.createNativeQuery("""
            SELECT p.id, p.title, p.description, COALESCE(c.name, ''), p.status,
                   p.activity_status, p.budget, p.views_count, COALESCE(p.photo_url, ''),
                   DATE_FORMAT(p.created_at, '%Y-%m-%d'), p.deadline,
                   p.experience_level, COALESCE(l.name, 'Remotely'),
                   EXISTS(SELECT 1 FROM flag f WHERE f.target_id = p.id AND f.target = 'FLAG_POSTING' AND f.status = 'PENDING'),
                   u.name, u.username, cl.user_id,
                   COALESCE(u.rating, 0), COALESCE(u.reviews_count, 0),
                   (SELECT GROUP_CONCAT(s.skill SEPARATOR '||') FROM posting_skills ps
                    JOIN skill s ON ps.skill_id = s.id WHERE ps.posting_id = p.id AND ps.activity_status = 'ACTIVE'),
                   (SELECT GROUP_CONCAT(pa.attachment_url SEPARATOR '||') FROM posting_attachments pa
                    WHERE pa.posting_id = p.id)
            FROM posting p
            LEFT JOIN category c ON p.category_id = c.id
            LEFT JOIN location l ON p.location_id = l.id
            JOIN client cl ON p.client_id = cl.id
            JOIN user u ON cl.user_id = u.id
            WHERE p.id = :id
            """);
        List<?> result = query.setParameter("id", id).getResultList();
        if (result.isEmpty()) return null;
        return (Object[]) result.get(0);
    }

    public List<Object[]> findOffersByPostingId(UUID postingId) {
        return em.createNativeQuery("""
            SELECT o.id, CONCAT(u.name, ' ', u.surname), COALESCE(u.photo_url, ''),
                   o.price, o.status, o.created_at, o.delivery_time
            FROM offer o
            JOIN freelancer f ON o.freelancer_id = f.id
            JOIN user u ON f.user_id = u.id
            WHERE o.posting_id = :id AND o.activity_status = 'ACTIVE'
            ORDER BY o.created_at DESC
            """)
            .setParameter("id", postingId)
            .getResultList();
    }

    public void deactivatePosting(UUID id) {
        em.createNativeQuery("UPDATE posting SET activity_status = 'INACTIVE' WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public void closePosting(UUID id) {
        em.createNativeQuery("UPDATE posting SET status = 'CLOSED' WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public void reopenPosting(UUID id) {
        em.createNativeQuery("UPDATE posting SET status = 'ACTIVE' WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public void reactivatePosting(UUID id) {
        em.createNativeQuery("UPDATE posting SET activity_status = 'ACTIVE' WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }
}
