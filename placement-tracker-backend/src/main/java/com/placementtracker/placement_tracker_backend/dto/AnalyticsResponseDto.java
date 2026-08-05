package com.placementtracker.placement_tracker_backend.dto;

import java.util.List;

public class AnalyticsResponseDto {

    private long totalStudents;
    private long totalCompanies;
    private long totalJobs;
    private long totalApplications;
    private long studentsPlaced;
    private double placementRate;
    private List<CompanyOfferCountDto> offersByCompany;

    public AnalyticsResponseDto(long totalStudents, long totalCompanies, long totalJobs,
                                long totalApplications, long studentsPlaced, double placementRate,
                                List<CompanyOfferCountDto> offersByCompany) {
        this.totalStudents = totalStudents;
        this.totalCompanies = totalCompanies;
        this.totalJobs = totalJobs;
        this.totalApplications = totalApplications;
        this.studentsPlaced = studentsPlaced;
        this.placementRate = placementRate;
        this.offersByCompany = offersByCompany;
    }

    public long getTotalStudents() { return totalStudents; }
    public long getTotalCompanies() { return totalCompanies; }
    public long getTotalJobs() { return totalJobs; }
    public long getTotalApplications() { return totalApplications; }
    public long getStudentsPlaced() { return studentsPlaced; }
    public double getPlacementRate() { return placementRate; }
    public List<CompanyOfferCountDto> getOffersByCompany() { return offersByCompany; }
}