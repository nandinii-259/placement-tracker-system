package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.OfferRequestDto;
import com.placementtracker.placement_tracker_backend.dto.OfferResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Offer;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.service.OfferService;
import com.placementtracker.placement_tracker_backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;
    private final StudentService studentService;

    public OfferController(OfferService offerService, StudentService studentService) {
        this.offerService = offerService;
        this.studentService = studentService;
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

    @PatchMapping("/application/{applicationId}/status")
    public ResponseEntity<OfferResponseDto> respondToOffer(@PathVariable Long applicationId,
                                                           @RequestBody Map<String, String> body) {
        Offer offer = offerService.getOfferByApplication(applicationId);

        if (!isAdmin()) {
            verifyOwnership(offer);
        }

        Offer.OfferStatus newStatus = Offer.OfferStatus.valueOf(body.get("status"));
        Offer updatedOffer = offerService.respondToOffer(applicationId, newStatus);

        return ResponseEntity.ok(toResponseDto(updatedOffer));
    }

    private void verifyOwnership(Offer offer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = auth.getName();

        Student loggedInStudent = studentService.getStudentByEmail(loggedInEmail);
        Long ownerStudentId = offer.getApplication().getStudent().getId();

        if (!loggedInStudent.getId().equals(ownerStudentId)) {
            throw new BusinessRuleException("You are not allowed to respond to another student's offer.");
        }
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private OfferResponseDto toResponseDto(Offer offer) {
        return new OfferResponseDto(
                offer.getId(),
                offer.getApplication().getId(),
                offer.getApplication().getStudent().getFullName(),
                offer.getApplication().getJob().getCompany().getName(),
                offer.getPositionTitle(),
                offer.getSalaryCtc(),
                offer.getOfferDate(),
                offer.getOfferStatus()
        );
    }
}