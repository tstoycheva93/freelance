package com.thesis.freelance.features.catalog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AdminCatalogRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> findAllCategories() {
        return em.createNativeQuery("""
            SELECT c.id, c.name,
                   EXISTS(SELECT 1 FROM skill s WHERE s.category_id = c.id AND s.activity_status = 'ACTIVE') AS in_use
            FROM category c
            WHERE c.activity_status = 'ACTIVE'
            ORDER BY c.name
            """).getResultList();
    }

    public List<Object[]> findAllSkills() {
        return em.createNativeQuery("""
            SELECT s.id, s.skill, c.name,
                   (EXISTS(SELECT 1 FROM freelancer_skills fs WHERE fs.skill_id = s.id AND fs.activity_status = 'ACTIVE')
                    OR EXISTS(SELECT 1 FROM posting_skills ps WHERE ps.skill_id = s.id AND ps.activity_status = 'ACTIVE')) AS in_use
            FROM skill s
            JOIN category c ON s.category_id = c.id
            WHERE s.activity_status = 'ACTIVE'
            ORDER BY s.skill
            """).getResultList();
    }

    public boolean isSkillInUse(UUID skillId) {
        long freelancerCount = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM freelancer_skills WHERE skill_id = :id AND activity_status = 'ACTIVE'")
                .setParameter("id", skillId).getSingleResult()).longValue();
        long postingCount = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM posting_skills WHERE skill_id = :id AND activity_status = 'ACTIVE'")
                .setParameter("id", skillId).getSingleResult()).longValue();
        return freelancerCount > 0 || postingCount > 0;
    }

    public boolean isCategoryInUse(UUID categoryId) {
        long count = ((Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM skill WHERE category_id = :id AND activity_status = 'ACTIVE'")
                .setParameter("id", categoryId).getSingleResult()).longValue();
        return count > 0;
    }

    public void deleteCategory(UUID id) {
        em.createNativeQuery("DELETE FROM category WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }

    public void deleteSkill(UUID id) {
        em.createNativeQuery("DELETE FROM skill WHERE id = :id")
                .setParameter("id", id).executeUpdate();
    }
}
