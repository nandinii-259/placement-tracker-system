package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.exception.ResourceNotFoundException;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.entity.Application;
import com.placementtracker.placement_tracker_backend.entity.Offer;
import com.placementtracker.placement_tracker_backend.repository.OfferRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final ApplicationService applicationService;

    public OfferService(OfferRepository offerRepository, ApplicationService applicationService) {
        this.offerRepository = offerRepository;
        this.applicationService = applicationService;
    }

    public Offer createOffer(Long applicationId, Offer offer) {
        Application application = applicationService.getApplicationById(applicationId);

        if (application.getStatus() != Application.Status.SELECTED) {
            throw new BusinessRuleException(
                    "An offer can only be created for a SELECTED application.");
        }

        if (offerRepository.findByApplicationId(applicationId).isPresent()) {
            throw new BusinessRuleException("An offer already exists for this application.");
        }

        offer.setApplication(application);
        Offer savedOffer = offerRepository.save(offer);

        applicationService.updateStatus(applicationId, Application.Status.OFFERED, null);

        return savedOffer;
    }

    public Offer getOfferByApplication(Long applicationId) {
        return offerRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("No offer found for this application."));
    }
}