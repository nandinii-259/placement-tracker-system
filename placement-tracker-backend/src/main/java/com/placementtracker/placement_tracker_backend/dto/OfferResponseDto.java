package com.placementtracker.placement_tracker_backend.dto;

import com.placementtracker.placement_tracker_backend.entity.Offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferResponseDto {

    private Long id;
    private Long applicationId;
    private String studentName;
    private String companyName;
    private String positionTitle;
    private BigDecimal salaryCtc;
    private LocalDate offerDate;
    private Offer.OfferStatus offerStatus;

    public OfferResponseDto(Long id, Long applicationId, String studentName, String companyName,
                            String positionTitle, BigDecimal salaryCtc, LocalDate offerDate,
                            Offer.OfferStatus offerStatus) {
        this.id = id;
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.companyName = companyName;
        this.positionTitle = positionTitle;
        this.salaryCtc = salaryCtc;
        this.offerDate = offerDate;
        this.offerStatus = offerStatus;
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public String getStudentName() { return studentName; }
    public String getCompanyName() { return companyName; }
    public String getPositionTitle() { return positionTitle; }
    public BigDecimal getSalaryCtc() { return salaryCtc; }
    public LocalDate getOfferDate() { return offerDate; }
    public Offer.OfferStatus getOfferStatus() { return offerStatus; }
}