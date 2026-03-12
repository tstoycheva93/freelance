package com.thesis.freelance.features.posting.repository;

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

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    List<Posting> findAllByClientUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    Page<Posting> findAllByClientUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.status = :status")
    Page<Posting> findAllByStatus(@Param("status") PostingStatus status, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location WHERE p.categoryId = :categoryId")
    Page<Posting> findAllByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location")
    Page<Posting> findAllWithClientAndLocation(Pageable pageable);

    @Query("SELECT p FROM Posting p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.location LEFT JOIN FETCH p.attachmentUrls WHERE p.id = :id")
    Posting findByIdWithClientAndLocation(@Param("id") UUID id);
}
