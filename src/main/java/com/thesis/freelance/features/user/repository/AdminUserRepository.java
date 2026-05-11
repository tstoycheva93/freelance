package com.thesis.freelance.features.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AdminUserRepository {

    @PersistenceContext
    private EntityManager em;

    public int countFiltered(String role, String status, String dateFrom) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `user` u JOIN user_role ur ON u.user_role = ur.id");
        sql.append(buildWhereClause(role, status, dateFrom));
        var query = em.createNativeQuery(sql.toString());
        applyParams(query, role, status, dateFrom);
        return ((Number) query.getSingleResult()).intValue();
    }

    public List<Object[]> findFiltered(String role, String status, String dateFrom, int page, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT u.id, u.name, u.surname, u.email, u.username, ur.role,
                   u.activity_status, u.created_at, u.birth_date, l.name,
                   u.photo_url, u.is_2fa_enabled, u.push_on, u.emails_on, u.is_verified,
                   u.rating, u.reviews_count
            FROM `user` u
            JOIN user_role ur ON u.user_role = ur.id
            LEFT JOIN location l ON u.location_id = l.id
            """);
        sql.append(buildWhereClause(role, status, dateFrom));
        sql.append(" ORDER BY u.created_at DESC");
        sql.append(" LIMIT :limit OFFSET :offset");

        var query = em.createNativeQuery(sql.toString());
        applyParams(query, role, status, dateFrom);
        query.setParameter("limit", size);
        query.setParameter("offset", (page - 1) * size);

        return query.getResultList();
    }

    public Object[] findByIdentifier(String identifier) {
        var query = em.createNativeQuery("""
            SELECT u.id, u.name, u.surname, u.email, u.username, ur.role,
                   u.activity_status, u.created_at, u.birth_date, l.name,
                   u.photo_url, u.is_2fa_enabled, u.push_on, u.emails_on, u.is_verified,
                   u.rating, u.reviews_count
            FROM `user` u
            JOIN user_role ur ON u.user_role = ur.id
            LEFT JOIN location l ON u.location_id = l.id
            WHERE u.id = :id OR u.username = :username
            LIMIT 1
            """);
        query.setParameter("id", parseUuidSafe(identifier));
        query.setParameter("username", identifier);

        List<?> result = query.getResultList();
        if (result.isEmpty()) return null;
        return (Object[]) result.get(0);
    }

    public long countTotalActiveUsers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM `user` WHERE activity_status = 'ACTIVE'")
                .getSingleResult()).longValue();
    }

    public long countNewThisMonth() {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM `user`
            WHERE activity_status = 'ACTIVE'
              AND created_at >= DATE_FORMAT(NOW(), '%Y-%m-01')
            """).getSingleResult()).longValue();
    }

    public long countVerifiedUsers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM `user` WHERE activity_status = 'ACTIVE' AND is_verified = 1")
                .getSingleResult()).longValue();
    }

    public long countPostingsByClient(UUID userId) {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM posting p
            JOIN client c ON p.client_id = c.id
            WHERE c.user_id = :userId AND p.activity_status = 'ACTIVE'
            """).setParameter("userId", userId).getSingleResult()).longValue();
    }

    public long countOffersSubmittedByFreelancer(UUID userId) {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM offer o
            JOIN freelancer f ON o.freelancer_id = f.id
            WHERE f.user_id = :userId AND o.activity_status = 'ACTIVE'
            """).setParameter("userId", userId).getSingleResult()).longValue();
    }

    public long countCompletedOffersForUser(UUID userId) {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM offer o
            JOIN freelancer f ON o.freelancer_id = f.id
            WHERE f.user_id = :userId
              AND o.status = 'COMPLETED'
              AND o.activity_status = 'ACTIVE'
            """).setParameter("userId", userId).getSingleResult()).longValue();
    }

    public List<Object[]> findRecentFlags(UUID userId, int limit) {
        return em.createNativeQuery("""
            SELECT f.reason, f.created_at, f.status
            FROM flag f
            WHERE f.target_id = :userId
            ORDER BY f.created_at DESC
            LIMIT :limit
            """)
            .setParameter("userId", userId)
            .setParameter("limit", limit)
            .getResultList();
    }

    private String buildWhereClause(String role, String status, String dateFrom) {
        List<String> conditions = new ArrayList<>();
        conditions.add("ur.role != 'ADMIN'");

        if (role != null && !role.isEmpty() && !role.equalsIgnoreCase("All Roles")) {
            conditions.add("ur.role = :role");
        }
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            conditions.add("u.activity_status = :status");
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            conditions.add("u.created_at >= :dateFrom");
        }

        return " WHERE " + String.join(" AND ", conditions);
    }

    private void applyParams(jakarta.persistence.Query query, String role, String status, String dateFrom) {
        if (role != null && !role.isEmpty() && !role.equalsIgnoreCase("All Roles")) {
            query.setParameter("role", role.toUpperCase());
        }
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All Statuses")) {
            query.setParameter("status", status.toUpperCase());
        }
        if (dateFrom != null && !dateFrom.isEmpty()) {
            query.setParameter("dateFrom", java.sql.Date.valueOf(dateFrom));
        }
    }

    private static Object parseUuidSafe(String identifier) {
        try {
            return java.util.UUID.fromString(identifier);
        } catch (IllegalArgumentException e) {
            return java.util.UUID.randomUUID();
        }
    }
}
