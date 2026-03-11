package com.thesis.freelance.features.user.repository;

import com.thesis.freelance.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userRole ur " +
            "LEFT JOIN FETCH u.location " +
            "WHERE u.activityStatus = 'ACTIVE' " +
            "AND (:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR ur.role = :role)")
    List<User> findAllBySearchAndRole(@Param("search") String search,
                                      @Param("role") String role,
                                      Sort sort);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.userRole ur " +
            "WHERE u.activityStatus = 'ACTIVE' " +
            "AND (:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:role IS NULL OR ur.role = :role)")
    Page<User> findBySearchAndRole(@Param("search") String search,
                                   @Param("role") String role,
                                   Pageable pageable);


}
