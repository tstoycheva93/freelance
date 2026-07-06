package com.thesis.freelance.features.posting.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.posting.entity.Posting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostingRepository extends JpaRepository<Posting, UUID> {
    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId AND p.activityStatus = :activityStatus")
    Page<Posting> findAllByClientUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    List<Posting> findAllByClientUserIdNoFilter(@Param("userId") UUID userId);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus AND p.categoryId = :categoryId")
    Page<Posting> findAllByCategoryId(@Param("activityStatus") ActivityStatus activityStatus, @Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus")
    Page<Posting> findAllWithClientAndLocation(@Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location LEFT JOIN FETCH p.attachmentUrls WHERE p.activityStatus = :activityStatus AND p.id = :id")
    Posting findByIdWithClientAndLocation(@Param("activityStatus") ActivityStatus activityStatus, @Param("id") UUID id);

    // Location filter
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus AND p.location.id = :locationId")
    Page<Posting> findAllByLocationId(@Param("activityStatus") ActivityStatus activityStatus, @Param("locationId") UUID locationId, Pageable pageable);

    // Search filter (title, description, client company name, location)
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location " +
            "WHERE p.activityStatus = :activityStatus " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.client.companyName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.location.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Posting> findAllBySearch(@Param("activityStatus") ActivityStatus activityStatus, @Param("search") String search, Pageable pageable);

    // Search + Location
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location " +
            "WHERE p.activityStatus = :activityStatus AND p.location.id = :locationId " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.client.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Posting> findAllBySearchAndLocation(@Param("activityStatus") ActivityStatus activityStatus, @Param("search") String search, @Param("locationId") UUID locationId, Pageable pageable);

    // Category + Location
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location " +
            "WHERE p.activityStatus = :activityStatus AND p.categoryId = :categoryId AND p.location.id = :locationId")
    Page<Posting> findAllByCategoryAndLocation(@Param("activityStatus") ActivityStatus activityStatus, @Param("categoryId") UUID categoryId, @Param("locationId") UUID locationId, Pageable pageable);

    // Search + Category + Location
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location " +
            "WHERE p.activityStatus = :activityStatus AND p.categoryId = :categoryId AND p.location.id = :locationId " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.client.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Posting> findAllBySearchAndCategoryAndLocation(@Param("activityStatus") ActivityStatus activityStatus, @Param("search") String search, @Param("categoryId") UUID categoryId, @Param("locationId") UUID locationId, Pageable pageable);

    // Status + Search + Category + Location
    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location " +
            "WHERE p.activityStatus = :activityStatus AND p.status = :status " +
            "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.client.companyName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(p.location.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:categoryId IS NULL OR p.categoryId = :categoryId) " +
            "AND (:locationId IS NULL OR p.location.id = :locationId)")
    Page<Posting> findAllByStatusAndSearchAndCategoryAndLocation(
            @Param("activityStatus") ActivityStatus activityStatus,
            @Param("status") PostingStatus status,
            @Param("search") String search,
            @Param("categoryId") UUID categoryId,
            @Param("locationId") UUID locationId,
            Pageable pageable);

    @Query("SELECT p FROM Posting p WHERE p.activityStatus = :activityStatus " +
            "AND p.status = com.thesis.freelance.common.enums.PostingStatus.ACTIVE " +
            "AND p.deadline IS NOT NULL AND p.deadline < :now")
    List<Posting> findExpiredActivePostings(
            @Param("activityStatus") ActivityStatus activityStatus,
            @Param("now") java.time.LocalDate now
    );
}
