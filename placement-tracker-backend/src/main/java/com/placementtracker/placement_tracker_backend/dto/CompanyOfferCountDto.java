package com.placementtracker.placement_tracker_backend.dto;

public class CompanyOfferCountDto {

    private String companyName;
    private long offerCount;

    public CompanyOfferCountDto(String companyName, long offerCount) {
        this.companyName = companyName;
        this.offerCount = offerCount;
    }

    public String getCompanyName() { return companyName; }
    public long getOfferCount() { return offerCount; }
}