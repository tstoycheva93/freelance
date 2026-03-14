package com.thesis.freelance.features.offer.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.features.offer.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
    @Query("SELECT o FROM Offer o WHERE o.freelancer.id = :freelancerId AND o.activityStatus = :activityStatus")
    List<Offer> findAllByFreelancer_Id(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId AND o.activityStatus = :activityStatus")
    List<Offer> findAllByFreelancerUserId(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId")
    List<Offer> findAllByFreelancerUserIdNoFilter(@Param("userId") UUID userId);

    // For freelancer - their offers to any posting
    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId AND o.activityStatus = :activityStatus ORDER BY o.posting.id ASC")
    Page<Offer> findByFreelancerUserIdAndActivityStatus(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId AND o.activityStatus = :activityStatus AND o.status = :status ORDER BY o.posting.id ASC")
    Page<Offer> findByFreelancerUserIdAndActivityStatusAndStatus(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);

    // For client - offers to their postings
    @Query("SELECT o FROM Offer o WHERE o.posting.client.userId = :clientUserId AND o.activityStatus = :activityStatus ORDER BY o.posting.id ASC")
    Page<Offer> findByPostingClientUserIdAndActivityStatus(@Param("clientUserId") UUID clientUserId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.posting.client.userId = :clientUserId AND o.activityStatus = :activityStatus AND o.status = :status ORDER BY o.posting.id ASC")
    Page<Offer> findByPostingClientUserIdAndActivityStatusAndStatus(@Param("clientUserId") UUID clientUserId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);

    // For specific posting (client owner only)
    @Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus")
    Page<Offer> findByPostingIdAndActivityStatus(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus AND o.status = :status")
    Page<Offer> findByPostingIdAndActivityStatusAndStatus(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);
}
