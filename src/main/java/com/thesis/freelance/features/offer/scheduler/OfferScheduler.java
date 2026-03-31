package com.thesis.freelance.features.offer.scheduler;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.common.enums.OfferStatus;
import com.thesis.freelance.common.enums.PostingStatus;
import com.thesis.freelance.features.notification.service.NotificationService;
import com.thesis.freelance.features.offer.entity.Offer;
import com.thesis.freelance.features.offer.repository.OfferRepository;
import com.thesis.freelance.features.posting.entity.Posting;
import com.thesis.freelance.features.posting.repository.PostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfferScheduler {

    private final PostingRepository postingRepository;
    private final OfferRepository offerRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void cancelOffersForExpiredDeadlines() {
        log.info("Running scheduled task: cancelOffersForExpiredDeadlines");
        
        List<Posting> activePostings = postingRepository.findAll().stream()
                .filter(p -> p.getStatus() == PostingStatus.ACTIVE)
                .filter(p -> p.getDeadline() != null && p.getDeadline().isBefore(LocalDate.now()))
                .toList();
        
        for (Posting posting : activePostings) {
            log.info("Posting {} has expired deadline, canceling all offers", posting.getId());

            var clientUserId = posting.getClient().getUserId();
            if (clientUserId != null) {
                notificationService.notifyPostingExpired(
                    clientUserId,
                    posting.getTitle(),
                        posting.getId()
                );
            }
            
            List<Offer> activeOffers = offerRepository.findActiveOffersByPostingId(posting.getId(), ActivityStatus.ACTIVE);
            for (Offer offer : activeOffers) {
                offer.setStatus(OfferStatus.CANCELED);
                offerRepository.save(offer);
                
                var freelancerUserId = offer.getFreelancer().getUserId();
                if (freelancerUserId != null) {
                    notificationService.notifyPostingExpired(
                        freelancerUserId,
                            posting.getTitle(),
                            posting.getId()
                    );
                }
            }
            
            posting.setStatus(PostingStatus.CLOSED);
            postingRepository.save(posting);
        }
        
        log.info("Expired posting check completed. Processed {} postings", activePostings.size());
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void cancelExpiredOffers() {
        log.info("Running scheduled task: cancelExpiredOffers");

        List<Offer> ongoingOffers = offerRepository.findAll().stream()
                .filter(o -> o.getActivityStatus() == ActivityStatus.ACTIVE)
                .filter(o -> o.getStatus() == OfferStatus.PENDING || o.getStatus() == OfferStatus.SHORTLISTED)
                .filter(o -> o.getDeliveryTime() != null && o.getDeliveryTime().isBefore(LocalDate.now()))
                .toList();

        for (Offer offer : ongoingOffers) {
            log.info("Pending/shortlisted offer {} is expired, canceling.", offer.getId());

            var clientUserId = offer.getPosting().getClient().getUserId();
            if (clientUserId != null) {
                notificationService.notifyOfferExpiredCanceled(
                        clientUserId,
                        offer.getPosting().getTitle(),
                        offer.getId()
                );
            }

            var freelancerUserId = offer.getFreelancer().getUserId();
            if (freelancerUserId != null) {
                notificationService.notifyOfferExpiredCanceled(
                        freelancerUserId,
                        offer.getPosting().getTitle(),
                        offer.getId()
                );
            }

            offer.setStatus(OfferStatus.CANCELED);
            offerRepository.save(offer);
        }

        List<Offer> acceptedAndDeliveredOffers = offerRepository.findAll().stream()
                .filter(o -> o.getActivityStatus() == ActivityStatus.ACTIVE)
                .filter(o -> o.getStatus() == OfferStatus.ACCEPTED || o.getStatus() == OfferStatus.DELIVERED)
                .filter(o -> o.getDeliveryTime() != null && o.getDeliveryTime().isBefore(LocalDate.now()))
                .toList();

        for (Offer offer : acceptedAndDeliveredOffers) {
            log.info("Accepted/delivered offer {} is expired.", offer.getId());

            var clientUserId = offer.getPosting().getClient().getUserId();
            if (clientUserId != null) {
                notificationService.notifyOfferExpiredClient(
                        clientUserId,
                        offer.getPosting().getTitle(),
                        offer.getId()
                );
            }

            var freelancerUserId = offer.getFreelancer().getUserId();
            if (freelancerUserId != null) {
                notificationService.notifyOfferExpiredFreelancer(
                        freelancerUserId,
                        offer.getPosting().getTitle(),
                        offer.getId()
                );
            }


        }



        log.info("Expired offer check completed. Processed {} offers", ongoingOffers.size());
    }
}
