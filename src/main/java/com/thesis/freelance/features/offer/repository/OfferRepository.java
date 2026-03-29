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
import java.util.List;
import java.util.Optional;
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
    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId AND o.activityStatus = :activityStatus")
    Page<Offer> findByFreelancerUserIdAndActivityStatus(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.freelancer.userId = :userId AND o.activityStatus = :activityStatus AND o.status = :status")
    Page<Offer> findByFreelancerUserIdAndActivityStatusAndStatus(@Param("userId") UUID userId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);

    // For client - offers to their postings
    @Query("SELECT o FROM Offer o WHERE o.posting.client.userId = :clientUserId AND o.activityStatus = :activityStatus")
    Page<Offer> findByPostingClientUserIdAndActivityStatus(@Param("clientUserId") UUID clientUserId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.posting.client.userId = :clientUserId AND o.activityStatus = :activityStatus AND o.status = :status")
    Page<Offer> findByPostingClientUserIdAndActivityStatusAndStatus(@Param("clientUserId") UUID clientUserId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);

    // For specific posting (client owner only)
    @Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus")
    Page<Offer> findByPostingIdAndActivityStatus(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus, Pageable pageable);

    @Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus AND o.status = :status")
    Page<Offer> findByPostingIdAndActivityStatusAndStatus(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status, Pageable pageable);

    // Check for existing active offer (pending/shortlisted/accepted)
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Offer o WHERE o.posting.id = :postingId AND o.freelancer.userId = :freelancerUserId AND o.activityStatus = :activityStatus AND o.status IN (com.thesis.freelance.common.enums.OfferStatus.PENDING, com.thesis.freelance.common.enums.OfferStatus.SHORTLISTED, com.thesis.freelance.common.enums.OfferStatus.ACCEPTED)")
    boolean hasActiveOfferByPostingAndFreelancer(@Param("postingId") UUID postingId, @Param("freelancerUserId") UUID freelancerUserId, @Param("activityStatus") com.thesis.freelance.common.enums.ActivityStatus activityStatus);

    // Get active offer ID
    @Query("SELECT o.id FROM Offer o WHERE o.posting.id = :postingId AND o.freelancer.userId = :freelancerUserId AND o.activityStatus = :activityStatus AND o.status IN (com.thesis.freelance.common.enums.OfferStatus.PENDING, com.thesis.freelance.common.enums.OfferStatus.SHORTLISTED, com.thesis.freelance.common.enums.OfferStatus.ACCEPTED) ORDER BY o.createdAt DESC")
    Optional<UUID> findActiveOfferIdByPostingAndFreelancer(@Param("postingId") UUID postingId, @Param("freelancerUserId") UUID freelancerUserId, @Param("activityStatus") com.thesis.freelance.common.enums.ActivityStatus activityStatus);

    // Find all active offers for a posting
    @Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus AND o.status IN (com.thesis.freelance.common.enums.OfferStatus.PENDING, com.thesis.freelance.common.enums.OfferStatus.SHORTLISTED)")
    List<Offer> findActiveOffersByPostingId(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus);

    // Find all active offers for a freelancer
    @Query("SELECT o FROM Offer o WHERE o.freelancer.id = :freelancerId AND o.activityStatus = :activityStatus AND o.status IN (com.thesis.freelance.common.enums.OfferStatus.PENDING, com.thesis.freelance.common.enums.OfferStatus.SHORTLISTED)")
    List<Offer> findActiveOffersByFreelancerId(@Param("freelancerId") UUID freelancerId, @Param("activityStatus") ActivityStatus activityStatus);

    // Count active offers for a posting (for reopen notification)
    @Query("SELECT COUNT(o) FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus AND o.status = :status")
    long countByPostingIdAndActivityStatusAndStatus(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus, @Param("status") OfferStatus status);

    // Count offers by status for a posting
    long countByPostingIdAndStatusIn(UUID postingId, List<OfferStatus> statuses);

    // Find offer by ID with attachments eager fetched
    @Query("SELECT o FROM Offer o LEFT JOIN FETCH o.attachments WHERE o.id = :offerId")
    Optional<Offer> findByIdWithAttachments(@Param("offerId") UUID offerId);

    // Find offer by ID with deliverables eager fetched
    @Query("SELECT o FROM Offer o LEFT JOIN FETCH o.deliverableUrls WHERE o.id = :offerId")
    Optional<Offer> findByIdWithDeliverables(@Param("offerId") UUID offerId);

    // Find offer by ID with both attachments and deliverables eager fetched
    @Query("SELECT o FROM Offer o WHERE o.id = :offerId")
    Optional<Offer> findByIdWithAttachmentsAndDeliverables(@Param("offerId") UUID offerId);

//za kato shte gi pravi completed
@Query("SELECT o FROM Offer o WHERE o.posting.id = :postingId AND o.activityStatus = :activityStatus AND o.status IN (com.thesis.freelance.common.enums.OfferStatus.ACCEPTED)")
    List<Offer> findAcceptedOffersByPostingId(@Param("postingId") UUID postingId, @Param("activityStatus") ActivityStatus activityStatus);
}
