package com.thesis.freelance.features.offer.repository;

import com.thesis.freelance.features.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    List<Offer> findAllByFreelancer_Id(UUID freelancerId);

    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId")
    List<Offer> findAllByFreelancerUserId(@Param("userId") UUID userId);
}
