package com.thesis.freelance.features.flag.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AdminFlagRepository {

    @PersistenceContext
    private EntityManager em;

    public int countFiltered(String status, String target) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM flag f WHERE 1=1");
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            sql.append(" AND f.status = :status");
        }
        if (target != null && !target.isEmpty() && !target.equalsIgnoreCase("All")) {
            sql.append(" AND f.target = :target");
        }
        var query = em.createNativeQuery(sql.toString());
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            query.setParameter("status", status.toUpperCase());
        }
        if (target != null && !target.isEmpty() && !target.equalsIgnoreCase("All")) {
            query.setParameter("target", target.toUpperCase());
        }
        return ((Number) query.getSingleResult()).intValue();
    }

    public List<Object[]> findFiltered(String status, String target, int page, int size) {
        StringBuilder sql = new StringBuilder("""
            SELECT f.id, f.target,
                   CASE f.target
                       WHEN 'FLAG_PROFILE' THEN CONCAT(up.name, ' ', up.surname)
                       WHEN 'FLAG_POSTING'  THEN COALESCE(p.title, '')
                       WHEN 'FLAG_REVIEW'   THEN CONCAT(ur.name, ' ', ur.surname, '''s Review')
                   END AS target_display,
                   f.type,
                   CONCAT(rep.name, ' ', rep.surname) AS reporter_name,
                   DATE_FORMAT(f.created_at, '%Y-%m-%d %H:%i') AS created_at,
                   f.status
            FROM flag f
            JOIN user rep ON f.reporter_id = rep.id
            LEFT JOIN user up ON f.target = 'FLAG_PROFILE' AND up.id = f.target_id
            LEFT JOIN posting p ON f.target = 'FLAG_POSTING' AND p.id = f.target_id
            LEFT JOIN review rv ON f.target = 'FLAG_REVIEW' AND rv.id = f.target_id
            LEFT JOIN user ur ON rv.reviewer_id = ur.id
            WHERE 1=1
            """);

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            sql.append(" AND f.status = :status");
        }
        if (target != null && !target.isEmpty() && !target.equalsIgnoreCase("All")) {
            sql.append(" AND f.target = :target");
        }
        sql.append(" ORDER BY f.created_at DESC LIMIT :limit OFFSET :offset");

        var query = em.createNativeQuery(sql.toString());
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
            query.setParameter("status", status.toUpperCase());
        }
        if (target != null && !target.isEmpty() && !target.equalsIgnoreCase("All")) {
            query.setParameter("target", target.toUpperCase());
        }
        query.setParameter("limit", size);
        query.setParameter("offset", (page - 1) * size);

        return query.getResultList();
    }

    public long countByStatus(String status) {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM flag WHERE status = :status")
                .setParameter("status", status)
                .getSingleResult()).longValue();
    }

    public long countTotal() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM flag").getSingleResult()).longValue();
    }

    public Object[] findFlagDetailById(UUID id) {
        var query = em.createNativeQuery("""
            SELECT f.id, f.target, f.target_id,
                   CASE f.target
                       WHEN 'FLAG_PROFILE' THEN CONCAT(up.name, ' ', up.surname)
                       WHEN 'FLAG_POSTING'  THEN COALESCE(p.title, '')
                       WHEN 'FLAG_REVIEW'   THEN CONCAT(ur.name, ' ', ur.surname, '''s Review')
                   END AS target_display,
                   f.type, f.status,
                   CONCAT(rep.name, ' ', rep.surname) AS reporter_name, rep.id AS reporter_user_id,
                   DATE_FORMAT(f.created_at, '%Y-%m-%d %H:%i') AS created_at,
                   CONCAT(rvwr.name, ' ', rvwr.surname) AS reviewer_name,
                   f.reason AS review_reason,
                   DATE_FORMAT(f.review_time, '%Y-%m-%d') AS review_time,
                   rv.content AS review_content
            FROM flag f
            JOIN user rep ON f.reporter_id = rep.id
            LEFT JOIN user up ON f.target = 'FLAG_PROFILE' AND up.id = f.target_id
            LEFT JOIN posting p ON f.target = 'FLAG_POSTING' AND p.id = f.target_id
            LEFT JOIN review rv ON f.target = 'FLAG_REVIEW' AND rv.id = f.target_id
            LEFT JOIN user ur ON rv.reviewer_id = ur.id
            LEFT JOIN user rvwr ON f.reviewer_id = rvwr.id
            WHERE f.id = :id
            """);
        List<?> result = query.setParameter("id", id).getResultList();
        if (result.isEmpty()) return null;
        return (Object[]) result.get(0);
    }

    public int reviewFlag(UUID id, String status, String reason, UUID reviewerId) {
        var query = em.createNativeQuery("""
            UPDATE flag SET status = :status, reason = :reason,
                reviewer_id = :reviewerId, review_time = CURDATE()
            WHERE id = :id AND status = 'PENDING'
            """);
        query.setParameter("id", id);
        query.setParameter("status", status);
        query.setParameter("reason", reason);
        query.setParameter("reviewerId", reviewerId);
        return query.executeUpdate();
    }
}
