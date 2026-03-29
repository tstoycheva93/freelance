package com.thesis.freelance.features.offer.repository;

import com.thesis.freelance.features.offer.entity.OfferContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferContractRepository extends JpaRepository<OfferContract, UUID> {
    Optional<OfferContract> findByOfferId(UUID offerId);
}
