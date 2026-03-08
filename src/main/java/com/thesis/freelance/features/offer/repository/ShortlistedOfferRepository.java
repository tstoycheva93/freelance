package com.thesis.freelance.features.offer.repository;

import com.thesis.freelance.features.offer.entity.ShortlistedOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShortlistedOfferRepository extends JpaRepository<ShortlistedOffer, UUID> {
    List<ShortlistedOffer> findByClientId(UUID clientId);
    boolean existsByClientIdAndOfferId(UUID clientId, UUID offerId);
    void deleteByClientIdAndOfferId(UUID clientId, UUID offerId);

    @Query("SELECT s FROM ShortlistedOffer s WHERE s.client.userId = :userId")
    List<ShortlistedOffer> findAllByClientUserId(@Param("userId") UUID userId);
}

