package com.thesis.freelance.features.user.repository;

import com.thesis.freelance.features.client.entity.Client;
import com.thesis.freelance.features.freelancer.entity.Freelancer;
import com.thesis.freelance.features.freelancer.entity.FreelancerSkills;
import com.thesis.freelance.features.skill.entity.Skill;
import com.thesis.freelance.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.userRole ur " +
            "LEFT JOIN u.location " +
            "LEFT JOIN Client c ON c.userId = u.id " +
            "LEFT JOIN Freelancer f ON f.userId = u.id " +
            "LEFT JOIN FreelancerSkills fs ON fs.freelancer.id = f.id " +
            "LEFT JOIN fs.skill s " +
            "WHERE u.activityStatus = 'ACTIVE' " +
            "AND u.isPublic = true " +
            "AND ur.role != 'ADMIN' " +
            "AND (:search IS NULL OR " +
            "   LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(c.occupation) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(f.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(s.skill) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR ur.role = :role)")
    List<User> findAllBySearchAndRole(@Param("search") String search,
                                      @Param("role") String role,
                                      Sort sort);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.userRole ur " +
            "LEFT JOIN u.location " +
            "LEFT JOIN Client c ON c.userId = u.id " +
            "LEFT JOIN Freelancer f ON f.userId = u.id " +
            "LEFT JOIN FreelancerSkills fs ON fs.freelancer.id = f.id " +
            "LEFT JOIN fs.skill s " +
            "WHERE u.activityStatus = 'ACTIVE' " +
            "AND u.isPublic = true " +
            "AND ur.role != 'ADMIN' " +
            "AND (:search IS NULL OR " +
            "   LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(c.occupation) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(f.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(s.skill) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR ur.role = :role)")
    Page<User> findAllBySearchAndRole(@Param("search") String search,
                                      @Param("role") String role,
                                      Pageable pageable);
    List<User> findByEmailsOnTrue();

    List<User> findByUserRole_RoleAndEmailsOnTrue(String role);

    List<User> findByUserRole_Role(String role);

    @Query("SELECT u FROM User u WHERE u.userRole.role != 'ADMIN'")
    List<User> findNonAdminUsers();

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since AND u.emailsOn = true")
    List<User> findRecentByEmailsOn(@Param("since") LocalDateTime since);


}
