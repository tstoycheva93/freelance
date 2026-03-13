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
    List<Posting> findAllByClient_Id(UUID clientId);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId AND p.activityStatus = :activityStatus")
    List<Posting> findAllByClientUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId AND p.activityStatus = :activityStatus")
    Page<Posting> findAllByClientUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    List<Posting> findAllByClientUserIdNoFilter(@Param("userId") UUID userId);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    Page<Posting> findAllByClientUserIdNoFilter(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus AND p.status = :status")
    Page<Posting> findAllByStatus(@Param("activityStatus") ActivityStatus activityStatus, @Param("status") PostingStatus status, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus AND p.categoryId = :categoryId")
    Page<Posting> findAllByCategoryId(@Param("activityStatus") ActivityStatus activityStatus, @Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.activityStatus = :activityStatus")
    Page<Posting> findAllWithClientAndLocation(@Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location LEFT JOIN FETCH p.attachmentUrls WHERE p.activityStatus = :activityStatus AND p.id = :id")
    Posting findByIdWithClientAndLocation(@Param("activityStatus") ActivityStatus activityStatus, @Param("id") UUID id);
}
