package com.thesis.freelance.features.transaction.repository;

import com.thesis.freelance.features.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByClientId(UUID clientId);

    List<Transaction> findByFreelancerId(UUID freelancerId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.freelancer.id = :freelancerId AND t.status = 'COMPLETED'")
    BigDecimal getTotalEarnedByFreelancer(@Param("freelancerId") UUID freelancerId);

    @Query("SELECT t FROM Transaction t WHERE t.client.id = :userId OR t.freelancer.id = :userId")
    List<Transaction> findAllByUserId(@Param("userId") UUID userId);
}