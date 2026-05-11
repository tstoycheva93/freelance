package com.thesis.freelance.features.statistics.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    public long countTotalActiveUsers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM `user` WHERE activity_status = 'ACTIVE'")
                .getSingleResult()).longValue();
    }

    public long countActivePostings() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM posting WHERE activity_status = 'ACTIVE' AND status = 'ACTIVE'")
                .getSingleResult()).longValue();
    }

    public long countCompletedOffers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM offer WHERE activity_status = 'ACTIVE' AND status = 'COMPLETED'")
                .getSingleResult()).longValue();
    }

    public long countNewRegistrationsThisWeek() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM `user` WHERE activity_status = 'ACTIVE' AND created_at >= DATE_SUB(NOW(), INTERVAL 1 WEEK)")
                .getSingleResult()).longValue();
    }

    public List<Object[]> findDailyUserGrowth(int days) {
        return em.createNativeQuery("""
            SELECT DATE_FORMAT(day, '%b %e') as day, cnt
            FROM (
                SELECT DATE(u.created_at) as day, COUNT(*) as cnt
                FROM `user` u
                WHERE u.activity_status = 'ACTIVE'
                  AND u.created_at >= DATE_SUB(NOW(), INTERVAL :days DAY)
                GROUP BY DATE(u.created_at)
                ORDER BY day
            ) sub
            """)
            .setParameter("days", days)
            .getResultList();
    }

    public long countPendingFlags() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM flag WHERE status = 'PENDING'")
                .getSingleResult()).longValue();
    }

    public long countInactiveUsers() {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM `user` WHERE activity_status = 'INACTIVE'")
                .getSingleResult()).longValue();
    }

    public Object[] findMaintenanceConfig() {
        List<?> result = em.createNativeQuery("SELECT maintenance_mode, maintenance_message FROM system_config LIMIT 1")
                .getResultList();
        if (result.isEmpty()) return new Object[]{false, ""};
        return (Object[]) result.get(0);
    }
}
