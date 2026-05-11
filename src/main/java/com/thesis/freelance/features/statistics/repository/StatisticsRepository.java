package com.thesis.freelance.features.statistics.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatisticsRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> findUserGrowth(int months) {
        return em.createNativeQuery("""
            SELECT DATE_FORMAT(u.created_at, '%b') as month,
                   SUM(CASE WHEN ur.role = 'FREELANCER' THEN 1 ELSE 0 END) as freelancers,
                   SUM(CASE WHEN ur.role = 'CLIENT' THEN 1 ELSE 0 END) as clients
            FROM `user` u
            JOIN user_role ur ON u.user_role = ur.id
            WHERE u.activity_status = 'ACTIVE'
              AND u.created_at >= DATE_SUB(NOW(), INTERVAL :months MONTH)
            GROUP BY YEAR(u.created_at), MONTH(u.created_at), DATE_FORMAT(u.created_at, '%b')
            ORDER BY MIN(u.created_at)
            """)
            .setParameter("months", months)
            .getResultList();
    }

    public List<Object[]> findOfferFunnel() {
        return em.createNativeQuery("""
            SELECT
              SUM(CASE WHEN o.status = 'PENDING' THEN 1 ELSE 0 END) as submitted,
              SUM(CASE WHEN o.status = 'ACCEPTED' THEN 1 ELSE 0 END) as accepted,
              SUM(CASE WHEN o.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed
            FROM offer o
            WHERE o.activity_status = 'ACTIVE'
            """)
            .getResultList();
    }

    public List<Object[]> findPostingActivity(int weeks) {
        return em.createNativeQuery("""
            SELECT YEARWEEK(p.created_at, 1) as period,
                   COUNT(*) as created
            FROM posting p
            WHERE p.activity_status = 'ACTIVE'
              AND p.created_at >= DATE_SUB(NOW(), INTERVAL :weeks WEEK)
            GROUP BY YEARWEEK(p.created_at, 1)
            ORDER BY period
            """)
            .setParameter("weeks", weeks)
            .getResultList();
    }

    public List<Object[]> findCompletedOffersPerWeek(int weeks) {
        return em.createNativeQuery("""
            SELECT YEARWEEK(o.updated_at, 1) as period,
                   COUNT(*) as completed
            FROM offer o
            WHERE o.activity_status = 'ACTIVE'
              AND o.status = 'COMPLETED'
              AND o.updated_at >= DATE_SUB(NOW(), INTERVAL :weeks WEEK)
            GROUP BY YEARWEEK(o.updated_at, 1)
            ORDER BY period
            """)
            .setParameter("weeks", weeks)
            .getResultList();
    }

    public List<Object[]> findClosedPostingsPerWeek(int weeks) {
        return em.createNativeQuery("""
            SELECT YEARWEEK(p.updated_at, 1) as period,
                   COUNT(*) as closed
            FROM posting p
            WHERE p.activity_status = 'ACTIVE'
              AND p.status = 'CLOSED'
              AND p.updated_at >= DATE_SUB(NOW(), INTERVAL :weeks WEEK)
            GROUP BY YEARWEEK(p.updated_at, 1)
            ORDER BY period
            """)
            .setParameter("weeks", weeks)
            .getResultList();
    }

    public List<Object[]> findTopCategories() {
        return em.createNativeQuery("""
            SELECT c.name, COUNT(p.id) as cnt
            FROM posting p
            JOIN category c ON p.category_id = c.id
            WHERE p.activity_status = 'ACTIVE'
              AND c.activity_status = 'ACTIVE'
            GROUP BY p.category_id, c.name
            ORDER BY cnt DESC
            """)
            .getResultList();
    }

    public List<Object[]> findGeoDistribution() {
        return em.createNativeQuery("""
            SELECT co.name as country, COUNT(u.id) as cnt
            FROM `user` u
            JOIN location l ON u.location_id = l.id
            JOIN country co ON l.country_id = co.id
            WHERE u.activity_status = 'ACTIVE'
            GROUP BY co.name
            ORDER BY cnt DESC
            """)
            .getResultList();
    }

    public List<Object[]> findTopFreelancers(int limit) {
        return em.createNativeQuery("""
            SELECT u.name, u.surname, u.rating, u.reviews_count, u.photo_url
            FROM `user` u
            JOIN user_role ur ON u.user_role = ur.id
            JOIN freelancer f ON f.user_id = u.id
            WHERE u.activity_status = 'ACTIVE'
              AND ur.role = 'FREELANCER'
              AND f.activity_status = 'ACTIVE'
            ORDER BY u.rating DESC, u.reviews_count DESC
            LIMIT :limit
            """)
            .setParameter("limit", limit)
            .getResultList();
    }
}
