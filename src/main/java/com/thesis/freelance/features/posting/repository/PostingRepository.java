package com.thesis.freelance.features.posting.repository;

import com.thesis.freelance.features.posting.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PostingRepository extends JpaRepository<Posting, UUID> {
    List<Posting> findAllByClient_Id(UUID clientId);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    List<Posting> findAllByClientUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Posting p WHERE p.client.userId = :userId")
    Page<Posting> findAllByClientUserId(@Param("userId") UUID userId, Pageable pageable);
}
