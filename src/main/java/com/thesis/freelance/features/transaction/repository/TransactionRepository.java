package com.thesis.freelance.features.transaction.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
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

    @Query("SELECT t FROM Transaction t WHERE t.client.id = :clientId AND t.activityStatus = :activityStatus")
    List<Transaction> findByClientId(@Param("clientId") UUID clientId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT t FROM Transaction t WHERE t.freelancer.id = :freelancerId AND t.activityStatus = :activityStatus")
    List<Transaction> findByFreelancerId(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.freelancer.id = :freelancerId AND t.status = 'COMPLETED' AND t.activityStatus = :activityStatus")
    BigDecimal getTotalEarnedByFreelancer(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT t FROM Transaction t WHERE (t.client.id = :userId OR t.freelancer.id = :userId) AND t.activityStatus = :activityStatus")
    List<Transaction> findAllByUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT t FROM Transaction t WHERE t.client.id = :userId OR t.freelancer.id = :userId")
    List<Transaction> findAllByUserIdNoFilter(@Param("userId") UUID userId);
}