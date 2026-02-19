package com.thesis.freelance.features.client.repository;

import com.thesis.freelance.features.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByUserIdId(UUID userId);

   /* @Query("SELECT COUNT(p) FROM Posting p WHERE p.client.id = :clientId")
    int countPostingsByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee.id = :userId")
    int countReviewsByUserId(@Param("userId") UUID userId);*/
}