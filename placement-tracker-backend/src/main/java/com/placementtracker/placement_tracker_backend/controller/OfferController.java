package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.OfferRequestDto;
import com.placementtracker.placement_tracker_backend.dto.OfferResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Offer;
import com.placementtracker.placement_tracker_backend.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<OfferResponseDto> createOffer(@PathVariable Long applicationId,
                                                        @Valid @RequestBody OfferRequestDto requestDto) {
        Offer offer = new Offer();
        offer.setPositionTitle(requestDto.getPositionTitle());
        offer.setSalaryCtc(requestDto.getSalaryCtc());

        Offer savedOffer = offerService.createOffer(applicationId, offer);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(savedOffer));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<OfferResponseDto> getOfferByApplication(@PathVariable Long applicationId) {
        Offer offer = offerService.getOfferByApplication(applicationId);
        return ResponseEntity.ok(toResponseDto(offer));
    }

    private OfferResponseDto toResponseDto(Offer offer) {
        return new OfferResponseDto(
                offer.getId(),
                offer.getApplication().getId(),
                offer.getApplication().getStudent().getFullName(),
                offer.getApplication().getJob().getCompany().getName(),
                offer.getPositionTitle(),
                offer.getSalaryCtc(),
                offer.getOfferDate()
        );
    }
}