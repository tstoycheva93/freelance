package com.thesis.freelance.features.offer.repository;

import com.thesis.freelance.features.offer.entity.OfferReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferReceiptRepository extends JpaRepository<OfferReceipt, UUID> {
    Optional<OfferReceipt> findByOfferId(UUID offerId);
    boolean existsByOfferId(UUID offerId);
    void deleteByOfferId(UUID offerId);
}
